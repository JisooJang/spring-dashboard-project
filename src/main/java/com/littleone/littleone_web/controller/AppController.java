package com.littleone.littleone_web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.littleone.littleone_web.api_response.DashboardDevice;
import com.littleone.littleone_web.command.MemberRegistRequest;
import com.littleone.littleone_web.domain.Bottle;
import com.littleone.littleone_web.domain.Device;
import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Infant_thumbnail;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.Peepee;
import com.littleone.littleone_web.domain.Temp;
import com.littleone.littleone_web.domain.Thumbnail;
import com.littleone.littleone_web.domain.WriteBottleData;
import com.littleone.littleone_web.domain.WritePeepeeData;
import com.littleone.littleone_web.service.AlarmTalkService;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.AppService;
import com.littleone.littleone_web.service.DashBoardService;
import com.littleone.littleone_web.service.DeviceService;
import com.littleone.littleone_web.service.DynamoDBService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MemberLogService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.ThumbnailService;

import net.coobird.thumbnailator.Thumbnails;

@Controller
public class AppController {
	@Autowired
	private DeviceService deviceService;

	@Autowired
	private MemberLogService logService;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService mngService;

	@Autowired
	private InfantService infantService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AmazonS3Service s3Service;

	@Autowired
	private DashBoardService dashboardService;

	@Autowired
	private ThumbnailService thumbnailService;

	@Autowired
	private AlarmTalkService talkService;

	@Autowired
	private AppService appService;

	@Autowired
	private DynamoDBService dbService;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;	// SimpleMessagingTemplate은 메시지를 먼저 받지 않아도 애플리케이션 내의 어느 곳에서든지 메시지를 전송한다

	@GetMapping("/app/join")
	public String get_join_app(Model model) {
		model.addAttribute("app", true);
		return "sign/person_join";
	}
	@ResponseBody
	@PostMapping("/app/join")
	public String join_app(@ModelAttribute @Valid MemberRegistRequest memberRegistRequest, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			System.out.println(bindingResult.getErrorCount());
			return "validation_error";	// DB에 추가하지 않고 폼으로 되돌아감
		}

		if(memberService.checkNickname(memberRegistRequest.getNickname()) == false) {
			return "nickname_error";
		}

		if(memberService.checkPassword(memberRegistRequest.getPassword()) == false) {
			return "password_error";
		}
		Member member = memberRegistRequest.switchToMember();

		if(memberService.checkDI(member.getDup_info()) != null) {
			return "dup";
		}

		if(!memberService.checkinputValue(member.getEmail()) || !memberService.checkinputValue(member.getPassword()) || !memberService.checkinputValue(member.getNickname())) {
			return "special";
		}

		String now_date = memberService.set_now_date();
		member.setPassword(memberService.encode_password(member.getPassword()));	// 비밀번호 암호화하여 저장
		member.setJoin_date(now_date);			// 오늘 날짜로 설정
		member.setPw_renew_date(now_date); 	// 오늘 날짜로 설정
		member.setSocial_code('x');
		member.setMember_type('p');

		member = memberService.join(member);
		if(member == null) {
			return "db";
		}

		int idx = memberService.getIdx(member.getEmail());
		Member_mng manage = new Member_mng();
		manage.setMember_idx(idx);
		manage.setPoint(0);
		manage.setGrade('1');
		if(mngService.insert(manage) == null) {	// DB에 저장
			return "db"; 
		}

		Thumbnail tn = new Thumbnail();
		tn.setMember_idx(idx);
		tn.setOriginal_filename("default-person.gif");
		tn.setServer_filename("default-person.gif");
		tn.setFile_size(4501);
		tn.setCategory('1');

		if(thumbnailService.insert(tn) == null) {
			return "db";
		}

		// 가입 축하 알림톡 발송
		talkService.send_welcome_msg(member.getPhone(), member.getName());
		return "success";
	}

	@ResponseBody
	@PostMapping("/app/login")
	public int login_app(HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {
		if(!memberService.checkinputValue(email) || !memberService.checkinputValue(password)) {
			return 0;
		}
		String device_model = request.getParameter("device_model"), device_version = request.getParameter("device_version");
		String now_date = memberService.set_now_date();
		HashMap<Integer, Member> map = memberService.login(email, password);

		if(map == null) {
			// 이메일이 존재하지않는 로그인 실패
			return -1;
		}
		if(map.get(1) != null) {
			Member member = map.get(1); 
			if(member.getMember_type() != 'p' && member.getMember_type() != 'c') {
				return 1;
			}

			// last_login_date 필드 설정
			mngService.update_login_date(member.getIdx(), now_date);	// 현재 날짜로 설정

			session.setAttribute("session_email", email);		// 개인 로그인시 세션 속성 이름 "personal", 기업 로그인시 세션 속성 이름 "corp"
			if(member.getMember_type() == 'p') {
				session.setAttribute("session_type", "personal");
			} else if(member.getMember_type() == 'c') {
				session.setAttribute("session_type", "corp");
			} 
			session.setAttribute("idx", member.getIdx());
			String thumbnail = member.getThumbnail();
			if(thumbnail != null && thumbnail.length() > 0) {
				session.setAttribute("session_thumbnail", thumbnail);
			}

			// 회원이 그룹이 존재할시 group_idx 세션에 저장
			Member_group group = groupService.findMember_group(member.getIdx());
			if(group != null) {
				session.setAttribute("group_idx", group.getGroup_idx());
			}
			//String ip_address = request.getHeader("ip_address");
			String ip_address = memberService.getClientIp(request);
			Member_log log = new Member_log(request.getSession().getId(), member.getIdx(), 'a', ip_address, device_model + "--" + device_version, memberService.set_now_time(), null);
			log = logService.save_log(log);
			if(log == null) {
				return 6;
			}

			response.addHeader("member_idx", member.getIdx() + "");
			response.addHeader("session_id", log.getSession_id());		// 로그인 성공시 member_idx와 session_token값 클라이언트에 전달

			List<Member_infant> mi = infantService.findByMember(member.getIdx());
			if(mi != null && mi.size() > 0) {
				response.addHeader("infant_idx", mi.get(0).getInfant().getIdx() + "");
			}

			// 비밀번호 변경 날짜가 100일이 지난경우
			if(memberService.checkPw_renew_date(member.getPw_renew_date()) >= 100) {
				return 2;
			} 
			return 5;

		} else {
			// 로그인 실패
			int result_code = 0;
			if(map.get(0) != null) {
				// 아이디는 존재하나 비밀번호 실패의 경우
				mngService.update_login_err_cnt(map.get(0).getIdx());	// 로그인 실패 횟수 증가. 0으로 초기화 시점 필요
				int err_cnt = mngService.getLoginErrCount(map.get(0).getIdx());
				if(err_cnt >= 5) {
					// 5회 이상 실패시 로그인 차단
					result_code = 3;
					if(err_cnt > 0) {
						result_code = 4;
					}
				}
			} 
			return result_code;
		}
	}

	@ResponseBody
	@PostMapping("/app/logout")
	public String app_logout(@RequestParam("session_id") String session_id, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			// 로그아웃 로그 저장
			int result = logService.logout(session_id, memberService.set_now_time());
			if(result != 1) {
				return "db_error";
			}
			return "success";
		} else {
			return "auth_failed";
		}
	}

	@ResponseBody
	@PostMapping("/app/get_token")
	public String get_token(@RequestParam("session_id") String session_id, @RequestParam("member_idx") int member_idx) {
		// 1. session_id, member_idx DB에서 검증
		// 2. 검증 완료되면 token을 발급하고 DB에 저장해둠.
		String token = memberService.getUniqId() + memberService.getUniqId();
		return token;
	}

	@ResponseBody
	@PostMapping("/app/register_device")
	public String register_device(@RequestParam("serial_num") String serial_num, @RequestParam("type") String device_type, HttpServletRequest request) {
		// 앱에서 로그인 여부 체크할것
		if(appService.check_auth_app(request) == true) {
			Device device = deviceService.save(new Device(Integer.parseInt(request.getParameter("member_idx")), serial_num, device_type.charAt(0), memberService.set_now_time()));
			if(device != null) {
				return "success";
			} else {
				return "db_failed";
			}
		} else {
			return "auth_failed";
		}
	}

	// 회원별 등록된 장비 정보 가져오기
	@ResponseBody
	@PostMapping("/app/get_devices")
	public List<Device> get_devices(@RequestParam("member_idx") int member_idx, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			List<Device> devices = deviceService.findByMember(member_idx);
			// device.type별 구분값 = temp : 1, peepee : 2, bottle : 3
			return devices;
		} else {
			return null;
		}
	}

	// 회원별 등록된 장비 수정
	@ResponseBody
	@PostMapping("/app/modify_devices/{device_type}")
	public String modify_devices(@RequestBody Device device, @PathVariable("device_type") String device_type, HttpServletRequest request) {
		if(appService.check_auth_app(device.getMember_idx() + "", (String)request.getHeader("session-id")) == true) {
			// type 요청값 검증
			char type;		// device.type별 구분값 = temp : 1, peepee : 2, bottle : 3
			if(device_type.equals("temp")) {
				type = '1';
			} else if(device_type.equals("peepee")) {
				type = '2';
			} else if(device_type.equals("bottle")) {
				type = '3';
			} else {
				return "device_type_failed";
			}
			
			
			// device 입력값 검증
			if(device.getUnit() != 'c' && device.getUnit() != 'f') {
				return "unit_valid_failed";
			}
			
			if(device.getSwitch_chk() != '1' && device.getSwitch_chk() != '0') {
				return "switch_valid_failed";
			}
			
			if(device.getSerial_num().trim().length() == 0 || device.getMac_address().trim().length() == 0) {
				return " validation_failed";
			}
			
			
			// device db update
			deviceService.update(device.getMember_idx(), type, device.getSerial_num(), device.getFirmware(), device.getMac_address(), 
					device.getUnit(), device.getSwitch_chk(), memberService.set_now_time());
			
			return "success";
		} else {
			return "auth_failed";
		}
	}

	@ResponseBody
	@PostMapping("/app/register_infant")
	public String postInfantInfo(@RequestParam("member_idx") String member_idx,
			@ModelAttribute Infant infant, @RequestParam(value="baby_file", required=false) MultipartFile thumbnail, HttpServletRequest request) throws IOException {
		// 로그인된 회원이 그룹에 속해있는지 확인한다.
		// 그룹이 있으면 infant에 group_idx를 설정하고 member_idx를 null로 설정한다.
		// 그룹이 없으면 infant에 group_idx를 null로 설정하고, member_idx를 로그인된 회원의 idx로 설정한다.

		if(appService.check_auth_app(request) == true) {
			Member member = memberService.findByIdx(Integer.parseInt(member_idx));

			if(member.getMember_infant().size() >= 2) {
					// 초기에는 아이는 1명만 등록가능. 나중에 확장할때 3명까지 등록가능하도록 수정할 것.
				return "infant_size_failed";
			}
			
			String infant_name = infant.getName();
			if(infant_name.contains(" ") || infant_name.length() == 0 || infant_name.length() > 20) {
				return "infant_name_failed";
			}

			// infant DB insert
			infant.setNationality(member.getCountry_code());
			Infant db_infant = infantService.insert(infant);
			if (db_infant == null) {
				return "db_failed";
			}

			// member-infant 관계테이블 insert
			Member_infant mi = new Member_infant();
			mi.setMember(member);
			mi.setInfant(infant);
			mi = groupService.insertMI(mi);
			if (mi == null) {
				return "db_failed";
			}

			if(thumbnail != null && thumbnail.getOriginalFilename() != null && thumbnail.getSize() > 0) {
				if(memberService.checkFileName(thumbnail.getOriginalFilename()) == false) {
					return "file_name_error";
				}
				if(thumbnail.getSize() > 2000000) {
					return "file_size_error";
				}

				String origin_file_name = thumbnail.getOriginalFilename();
				String server_file_name = memberService.getUniqFileName(origin_file_name);

				InputStream input = null;
				BufferedImage image = null;
				BufferedImage bufferedImage = null;

				try {
					input = thumbnail.getInputStream();
					image = ImageIO.read(input);
					bufferedImage = Thumbnails.of(image).size(200, 200).outputQuality(1.0f).asBufferedImage();	// 업로드된 썸네일 이미지 크롭

					// aws s3에 thumbnail object upload.
					String extension = origin_file_name.substring(origin_file_name.indexOf('.') + 1).toLowerCase();
					s3Service.putThumbnailBI("thumbnail/infant", server_file_name, extension, bufferedImage);	// put object to amazon s3
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "file_exception_error";
				} finally {
					if(input != null) {
						input.close();
						input = null;
					}
					if(image != null) {
						image.flush();
						image = null;
					}
					if(bufferedImage != null) {
						bufferedImage.flush();
						bufferedImage = null;
					}
				}

				Infant_thumbnail it = new Infant_thumbnail(db_infant.getIdx(), origin_file_name, server_file_name, "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/infant/" + server_file_name);
				it = dashboardService.saveInfantThumbnail(it);
				if(it == null) {
					return "db_failed";
				}
			}

			return "success";
		} else {
			return "auth_failed";
		}

	}

	//아기정보수정
	@ResponseBody
	@PostMapping("/app/modify_infant")
	public Infant get_modify_infant(HttpServletRequest request) {
		String member_idx = request.getParameter("member_idx");
		if(appService.check_auth_app(request) == true) {
			List<Member_infant> lists =  infantService.findByMember(Integer.parseInt(member_idx));
			if(lists != null && lists.size() > 0) {
				return infantService.findByMember(Integer.parseInt(member_idx)).get(0).getInfant();	// 임시로 첫번째 아이 리턴
			} else {
				// 인증 성공 + 아기정보 없음
				return null;
			}

		} else {
			//아기정보요청 : 인증 실패
			return null;	// auth_failed
		}

	}

	//아기정보수정
	@ResponseBody
	@PostMapping("/app/modify_infant/post")
	public String post_modify_infant(@RequestParam("member_idx") String member_idx, HttpServletRequest request, @ModelAttribute Infant infant) {
		String infant_idx = request.getParameter("infant_idx");

		if(appService.check_auth_app(request) == true) {
			Member m = memberService.findByIdx(Integer.parseInt(member_idx));
			infantService.updateInfant(Integer.parseInt(infant_idx), infant.getName(), infant.getBirth(), infant.getSex(), infant.getWeight(), infant.getHeight(), infant.getBlood_type());
			return "success";
		} else {
			return "auth_failed";
		}
	}

	// 아기 썸네일 등록
	@ResponseBody
	@PostMapping("/app/modify_infant/thumbnail")
	public String post_modify_infant_thumb(@RequestParam("member_idx") String member_idx, @RequestParam("thumbnail") MultipartFile thumbnail, @RequestParam("infant_idx") int infant_idx, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			Member_infant mi = groupService.findMI(Integer.parseInt(member_idx), infant_idx);
			if(mi != null) {
				Infant_thumbnail tn = new Infant_thumbnail();
				String server_file_name = memberService.getUniqFileName(thumbnail.getOriginalFilename());
				tn.setInfant_idx(infant_idx);
				tn.setOrigin_file(thumbnail.getOriginalFilename());
				tn.setServer_file(server_file_name);
				String url = "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/infant/" + server_file_name;
				tn.setUrl(url);
				tn = infantService.saveThumbnail(tn);
				if(tn != null) {
					return "success";
				} else {
					return "db_error";
				}

			} else {
				return "infant_failed";
			}

		} else {
			return "auth_failed";
		}

	}

	@ResponseBody
	@PostMapping("/app/batch_save/temp")
	public String batchSaveTemp(@RequestParam("temps") List<Temp> temps, HttpServletRequest request) {
		String member_idx = request.getHeader("member-idx");
		String session_id = request.getHeader("session-id");
		String serial_num = request.getHeader("serial-num");
		
		if(appService.check_auth_app(member_idx, session_id) == false) {
			return "auth_failed";	
		} 
		
		if(deviceService.findBySerialNumAndType(Integer.parseInt(member_idx), serial_num, '1') == null) {
			return "serial_num_failed";
		}
		
		dbService.batchSaveTemp(temps);
		return "success";

	}

	@ResponseBody
	@PostMapping("/app/batch_save/peepee/{event}")
	public String batchSavePeepee(@RequestBody WritePeepeeData data, @PathVariable("event") String event, HttpServletRequest request) {
		if(appService.check_auth_app(data.getMember_idx(), data.getSession_id()) == true) {

			if(deviceService.findBySerialNumAndType(Integer.parseInt(data.getMember_idx()), data.getSerial_num(), '2') == null) {
				return "serial_num_failed";
			}

			if(event.equals("defec")) {
				/*				String member_idx = request.getParameter("member_idx");
				Alert alert = new Alert();
				alert.setRequester(0);
				alert.setRecipient(Integer.parseInt(member_idx));*/

				// 배변활동 헤더 알림 전송 (배변활동 알림 - 전송데이터 없음. 소켓 알림만)
				messagingTemplate.convertAndSend("/topic/peepee/" + data.getPeepees().get(0).getInfant_idx(), memberService.set_now_time());

				// 배변활동 알림 대쉬보드로 전송 (일별 총 배변횟수)
				List<Peepee> peepees = data.getPeepees();
				String last_time = peepees.get(peepees.size() - 1).getDate_time() + "";

				DashboardDevice dashboard_data = new DashboardDevice();

				dashboard_data.setTotal_count(dbService.readPeepeeCountByDate(peepees.get(0).getInfant_idx() + "", dbService.get_now_date() + ""));
				dashboard_data.setDate_time(last_time.substring(8, 10) + ":" + last_time.substring(10, 12) + ":" + last_time.substring(12));

				messagingTemplate.convertAndSend("/topic/dashboard/peepee/" + data.getPeepees().get(0).getInfant_idx(), dashboard_data);
			} else if(event.equals("change")) {
				// 기저귀 교체활동 알림 대쉬보드로 전송
				//messagingTemplate.convertAndSend("/queue/dashboard/peepee/change" + peepees.get(0).getInfant_idx(), peepees.get(0));
			}

			dbService.batchSavePeepee(data.getPeepees());
			return "success";
		} else {
			return "auth_failed";
		}


	}

	@ResponseBody
	@PostMapping("/app/batch_save/bottle")
	public String batchSaveBottle(@RequestBody WriteBottleData data, HttpServletRequest request) {
		if(appService.check_auth_app(data.getMember_idx(), data.getSession_id()) == true) {

			if(deviceService.findBySerialNumAndType(Integer.parseInt(data.getMember_idx()), data.getSerial_num(), '3') == null) {
				return "serial_num_failed";
			}

			List<Bottle> bottles = data.getBottles();
			String last_time = bottles.get(bottles.size() - 1).getDate_time() + "";

			DashboardDevice dashboard_data = new DashboardDevice();
			dashboard_data.setTotal_count(dbService.readPeepeeCountByDate(bottles.get(0).getInfant_idx() + "", dbService.get_now_date() + ""));
			dashboard_data.setDate_time(last_time.substring(8, 10) + ":" + last_time.substring(10, 12) + ":" + last_time.substring(12));

			// 수유활동 헤더 알림 전송 (전송 데이터 없음. 소켓알림만)
			messagingTemplate.convertAndSend("/topic/bottle/" + data.getBottles().get(0).getInfant_idx(), memberService.set_now_time());
			// 수유활동 대쉬보드 알림 전송 (마지막 수유 시간)
			messagingTemplate.convertAndSend("/topic/dashboard/bottle/" + data.getBottles().get(0).getInfant_idx(), dashboard_data);	// 소켓전송(대쉬보드 수치 변경 : 마지막 수유 시간)
			dbService.batchSaveBottle(data.getBottles());
			return "success";
		} else {
			return "auth_failed";
		}


	}

}
