package com.littleone.littleone_web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.littleone.littleone_web.api_request.DiaryModifyFile;
import com.littleone.littleone_web.api_response.BabybookResponse;
import com.littleone.littleone_web.api_response.DiaryResponse;
import com.littleone.littleone_web.api_response.Infant_diary_response;
import com.littleone.littleone_web.api_response.Infant_schedule_response;
import com.littleone.littleone_web.api_response.ScheduleResponse;
import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.BabyBook;
import com.littleone.littleone_web.domain.Dashboard;
import com.littleone.littleone_web.domain.Device_response;
import com.littleone.littleone_web.domain.GroupInfo;
import com.littleone.littleone_web.domain.Id_file;
import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Infant_diary;
import com.littleone.littleone_web.domain.Infant_schedule;
import com.littleone.littleone_web.domain.Infant_thumbnail;
import com.littleone.littleone_web.domain.JsonDashboard;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.service.AlarmTalkService;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.DashBoardService;
import com.littleone.littleone_web.service.DeviceService;
import com.littleone.littleone_web.service.DynamoDBService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberService;

import net.coobird.thumbnailator.Thumbnails;

@Controller
public class DashBoardController {

	@Autowired
	private InfantService infantService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AlertService alarmService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private DashBoardService dashboardService;

	@Autowired
	private MailSenderService mailService;

	@Autowired
	private AmazonS3Service s3Service;

	@Autowired
	private AlarmTalkService kakaoService;

	@Autowired
	private DynamoDBService dbService;


	@GetMapping("/dashboard")
	public String get_dashboard(HttpSession session, Model model) {
		dashboard_index_setting(memberService.set_now_date(), session, model);
		return "dashboard/dashboard";
	}


	// 대쉬보드에서 특정 날짜 선택할시 다시 리로드되는 대쉬보드 메인
	@GetMapping("/dashboard/select_next/{date}")
	public String get_dashboard_byDateNext(@PathVariable("date") String date, HttpServletRequest request, HttpSession session, Model model) throws ParseException {
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date_obj.setTime(date_obj.getTime() + (long)(1000 * 60 * 60 * 24));

		/*if(info != null) {
			info.setDate(sdf.format(date_obj));
			session.setAttribute("dashboard_info", info);
		}*/
		dashboard_index_setting(sdf.format(date_obj), session, model);

		//return "redirect:/dashboard";
		return "dashboard/dashboard";
	}

	// 대쉬보드에서 특정 아이 선택할시 다시 리로드되는 대쉬보드 메인
	@GetMapping("/dashboard/select_infant/{infant_idx}")
	public String select_infant(@PathVariable("infant_idx") int infant_idx, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		if(infantService.find(member_idx, infant_idx) != null) {	// 전달된 아이번호가 세션 회원의 아이가 맞으면
			session.setAttribute("infant_idx", infant_idx);
		}
		return "redirect:/dashboard";
	}

	// 대쉬보드에서 특정 날짜, 특정 아이 선택할시 다시 리로드되는 대쉬보드 메인
	@GetMapping("/dashboard/select_before/{date}")
	public String get_dashboard_byDateBefore(@PathVariable("date") String date, HttpServletRequest request, HttpSession session, Model model) throws ParseException {
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date_obj.setTime(date_obj.getTime() - (long)(1000 * 60 * 60 * 24));

		dashboard_index_setting(sdf.format(date_obj), session, model);

		return "dashboard/dashboard";
	}

	// 대쉬보드에서 특정 날짜, 특정 아이 선택할시 다시 리로드되는 대쉬보드 메인
	@GetMapping("/dashboard/select/{date}/{infant_idx}")
	public String get_dashboard_byDate(@PathVariable("date") String date, @PathVariable("infant_idx") int infant_idx, HttpSession session, Model model) throws ParseException {
		int member_idx = (int) session.getAttribute("idx");
		if(infantService.find(member_idx, infant_idx) != null) {
			// 전달된 아이번호가 세션 회원의 아이가 맞으면
			session.setAttribute("selected_infant", infant_idx);
			GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
			Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date_obj.setTime(date_obj.getTime() - (long)(1000 * 60 * 60 * 24));
			if(info != null) {
				info.setShared_infant(infantService.findOne(infant_idx));
				info.setDate(date);
				session.setAttribute("dashboard_info", info);
			}
		}
		return "redirect:/dashboard";
	}

	// 아기정보 등록 post
	@ResponseBody
	@PostMapping("/dashboard/register_infant")
	public String postInfantInfo(@ModelAttribute Infant infant, @RequestParam(value="baby_file", required=false) MultipartFile thumbnail,
			@RequestParam(value="originName", required=false) String origin_file_name, HttpSession session) throws IOException {
		// 로그인된 회원이 그룹에 속해있는지 확인한다.
		// 그룹이 있으면 infant에 group_idx를 설정하고 member_idx를 null로 설정한다.
		// 그룹이 없으면 infant에 group_idx를 null로 설정하고, member_idx를 로그인된 회원의 idx로 설정한다.

		int idx = (int) session.getAttribute("idx");
		Member member = memberService.findByIdx(idx);
		boolean thumb_check = false;
		String server_file_name = null;

		Member_group group = groupService.findMember_group(idx);
		if(group != null) {
			return "group_failed";
		}
		if(member.getMember_infant().size() >= 1) {
			return "infant_size_failed";
		}

		
		// 띄어쓰기, 특수문자 불가
		String infant_name = infant.getName();
		String regex  = "^[a-zA-Z가-힣0-9]*$"; 	// 특수문자 필터링
		if(infant_name == null || infant_name.contains(" ") || infant.getName().length() == 0 || infant_name.length() > 20 
				|| !infant_name.matches(regex)) {
			return "infant_name_failed";
		}

		if(infant.getSex() != 'f' && infant.getSex() != 'm') {
			return "infant_gender_failed";
		}

		if(infant.getBirth() == null || infant.getBirth().trim().length() != 8) {
			return "infant_birth_failed";
		}

		// 소수점 2자리까지 가능하도록 검증 (점)
		String height_regex = "^([0-9]{2,3})(\\.[0-9]{1,2})?$";
		String height = Float.toString(infant.getHeight());
		if(height == null || height.trim().length() == 0 || !height.matches(height_regex)) {
			return "infant_height_failed";
		}

		String weight_regex = "^[0-9]{1,2}(\\.[0-9]{1,2})?$";
		String weight = Float.toString(infant.getWeight());
		if(weight == null || weight.trim().length() == 0 || !weight.matches(weight_regex)) {
			return "infant_weight_failed";
		}

		String blood_type = infant.getBlood_type();
		if(!blood_type.equals("a") && !blood_type.equals("b") && !blood_type.equals("o") && !blood_type.equals("ab")) {
			return "infant_blood_type_failed";
		}


		if(thumbnail != null && origin_file_name != null && thumbnail.getSize() > 0) {
			thumb_check = true;
			if(memberService.checkFileName(origin_file_name) == false) {
				return "file_name_error";
			}
			if(thumbnail.getSize() > 3000000) {
				return "file_size_error";
			}
			if(origin_file_name.getBytes().length > 50) {
				return "file_name_size_error";
			}

			server_file_name = memberService.getUniqFileName(origin_file_name);

			InputStream input = null;
			BufferedImage image = null;

			try {
				input = thumbnail.getInputStream();
				image = ImageIO.read(input);

				// aws s3에 thumbnail object upload.
				String extension = origin_file_name.substring(origin_file_name.indexOf('.') + 1).toLowerCase();
				s3Service.putThumbnailBI("thumbnail/infant", server_file_name, extension, image);	// put object to amazon s3
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
			}

		}

		// infant DB insert
		//infant.setNationality("로그인된 회원의 국적으로 설정");
		Infant db_infant = infantService.insert(infant);
		if (db_infant == null) {
			return "failed";
		}

		// member-infant 관계테이블 insert
		Member_infant mi = new Member_infant();
		mi.setMember(member);
		mi.setInfant(infant);
		mi = groupService.insertMI(mi);
		if (mi == null) {
			return "failed";
		}

		if(thumb_check) {
			Infant_thumbnail it = new Infant_thumbnail(db_infant.getIdx(), origin_file_name, server_file_name, "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/infant/" + server_file_name);
			it = dashboardService.saveInfantThumbnail(it);
			if(it == null) {
				return "failed";
			}
		}

		session.setAttribute("infant_idx", db_infant.getIdx());
		return "success";

	}

	/* 아이 정보 불러오기 */
	@ResponseBody
	@GetMapping("/dashboard/infant_info/{infant_idx}")
	public Infant get_infant_info(@PathVariable("infant_idx") int infant_idx, HttpSession session, HttpServletResponse response) {
		if (session.getAttribute("idx") == null) {
			return null;
		}
		Infant infant = infantService.findOne(infant_idx);
		if (infant == null) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = null;

			try {
				out = response.getWriter();
				out.println("<script>alert('select된 아기 데이터가 없습니다'); history.go(-1);</script>");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}

			return null;
		}
		int member_idx = (int) session.getAttribute("idx");
		if (infantService.find(member_idx, infant_idx) != null) {
			//member와 infant가 연관관계에 있으면
			return infant;
		} else {
			return null;
		}
	}

	/* 아이 정보 수정 post */
	@ResponseBody
	@PostMapping("/dashboard/modify_infant_info/{infant_idx}")
	public String modify_infant_info(@PathVariable("infant_idx") int infant_idx,
			@RequestParam("weight") String weight, @RequestParam("height") String height, @RequestParam("birth") String birth, 
			@RequestParam("blood_type") String blood_type, @RequestParam("name") String name, @RequestParam("sex") char sex,
			@RequestParam(name="thumbnail", required=false) MultipartFile thumbnail, 
			@RequestParam(name="originName", required=false) String original_filename, HttpSession session) throws IOException {
		int member_idx = (int) session.getAttribute("idx");
		Member_infant mi = groupService.findMI(member_idx, infant_idx);
		if(mi == null) {
			return "infant_auth_failed";
		}

		String regex  = "^[a-zA-Z가-힣0-9]*$"; 	// 특수문자 필터링
		if(name == null || name.contains(" ") || name.length() == 0 || name.length() > 20 || !name.matches(regex)) {
			return "infant_name_failed";
		}

		if(sex != 'f' && sex != 'm') {
			return "infant_gender_failed";
		}

		if(birth == null || birth.trim().length() != 8) {
			return "infant_birth_failed";
		}

		String height_regex = "^([0-9]{2,3})(\\.[0-9]{1,2})?$";
		if(height == null || height.trim().length() == 0 || !height.matches(height_regex)) {
			return "infant_height_failed";
		}

		String weight_regex = "^[0-9]{1,2}(\\.[0-9]{1,2})?$";
		if(weight == null || weight.trim().length() == 0 || !weight.matches(weight_regex)) {
			return "infant_weight_failed";
		}

		if(!blood_type.equalsIgnoreCase("a") && !blood_type.equalsIgnoreCase("b") && !blood_type.equalsIgnoreCase("o") && !blood_type.equalsIgnoreCase("ab")) {
			return "infant_blood_type_failed";
		}

		// 썸네일이 첨부되었으면
		if(thumbnail != null && thumbnail.isEmpty() == false) {
			if(thumbnail.getSize() > 3000000) {
				return "file_size_error";
			}

			if(original_filename.getBytes().length > 50) {
				return "file_name_size_error";
			}

			String server_filename = memberService.getUniqFileName(original_filename);
			if(memberService.checkFileName(original_filename) == false) {
				return "file_name_error";
			}

			String extension = original_filename.substring(original_filename.indexOf('.') + 1).toLowerCase();

			// 1. S3에 기존 썸네일 삭제 및 새로운 썸네일 크롭후 업로드
			Infant_thumbnail thumb = infantService.findThumbnail(infant_idx);
			if(thumb != null) {
				s3Service.deleteThumbnail("thumbnail/infant", thumb.getServer_file());
			}

			InputStream input = null;
			BufferedImage image = null;

			try {
				input = thumbnail.getInputStream();
				image = ImageIO.read(input);

				s3Service.putThumbnailBI("thumbnail/infant", server_filename, extension, image);

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
			}

			// 2. infant_thumbnail 행 업데이트
			String s3_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/infant/" + server_filename;
			if(thumb != null) {
				infantService.updateThumbanil(infant_idx, original_filename, server_filename, s3_url);
			} else {
				infantService.saveThumbnail(new Infant_thumbnail(infant_idx, original_filename, server_filename, s3_url));
			}

		}

		// 아이 정보 수정
		infantService.updateInfant(infant_idx, name, birth, sex, Float.parseFloat(weight), Float.parseFloat(height), blood_type.toLowerCase());
		return "success";
	}

	@ResponseBody
	@GetMapping("/dashboard/delete_infant/{infant_idx}")
	public String delete_infant(@PathVariable("infant_idx") int infant_idx, HttpSession session) {
		Integer session_infant_idx = (Integer) session.getAttribute("infant_idx");

		if(session_infant_idx == null) {
			return "no_infant";
		}
		if(infant_idx != session_infant_idx) {
			return "no_authority";
		}

		if(infantService.find((int)session.getAttribute("idx"), infant_idx) != null) {	// 세션회원의 아이가 맞으면
			infantService.delete(infant_idx);
			session.removeAttribute("infant_idx");	// 현재 선택된 아이를 삭제하는 것이므로 세션에서 아이값을 지워준다.
			
			// 그룹이 존재할경우 아이정보를 지우면 그룹도 같이 지워준다.
			Integer group_idx = (Integer) session.getAttribute("group_idx");
			if(group_idx != null) {
				groupService.deleteGroup(group_idx);
				session.removeAttribute("group_idx");
			}
			return "success";
		} else {
			return "failed";
		}
	}

	/* 메일로 리틀원 가입 초대장 보내기 */
	@ResponseBody
	@PostMapping("/dashboard/invite_family")
	public String invite_by_email(@RequestParam("invite_email") String[] email, HttpSession session) {
		Integer group_idx = (Integer) session.getAttribute("group_idx");
		if(group_idx != null) {
			if(groupService.findByGroup_idx(group_idx).size() >= 5) {
				return "group_size_error";
			}
		}

		if(email.length > 5) {
			return "email_length_failed";
		}

		for(int i=0 ; i<email.length ; i++) {
			Member m = memberService.findByEmail(email[i]);
			if(m != null) {
				return "already_member:" + email[i];
			}
		}

		int idx = (int) session.getAttribute("idx");
		String invite_url = "https://littleone.kr/join_personal/family_invite/" + idx;
		//String invite_url = "http://localhost:8080/join_personal/family_invite/" + idx;
		Member member = memberService.findByIdx(idx);
		Map<String, String> template_data = new HashMap<String, String>();
		template_data.put("invite_url", invite_url);
		template_data.put("member_name", member.getNickname());

		Collection<String> receiver = new ArrayList<String>();
		for(int i=0 ; i<email.length ; i++) {
			receiver.add(email[i]);
		}
		// 리틀원 가입 초대장 메일 템플릿 등록 필요
		mailService.sendMail(receiver, "smart@littleone.kr", "invite_family", template_data);
		return "success";
	}

	@ResponseBody
	@PostMapping("/dashboard/invite_family/kakao")
	public String invite_by_kakao(@RequestParam("phone") String phone, HttpSession session) {
		Integer group_idx = (Integer) session.getAttribute("group_idx");
		if(group_idx != null) {
			if(groupService.findByGroup_idx(group_idx).size() >= 5) {
				return "group_size_error";
			}
		}

		if(phone.length() != 11) {
			return "phone_length_error";
		}

		String phone2 = "+82" + phone.substring(1);

		List<String> find_result = memberService.find_email(phone2);
		if(find_result != null && !find_result.isEmpty()) {
			return "already_member_error";
		}

		int idx = (int)session.getAttribute("idx");
		Member m = memberService.findByIdx(idx);

		String invite_url = "https://littleone.kr/join_personal/family_invite/" + idx;
		//String invite_url = "http://localhost:8080/join_personal/family_invite/" + idx;
		String name = m.getName() != null ? m.getName() : m.getNickname();
		kakaoService.send_invite_msg(phone, name, invite_url);

		return "success";

	}

	@GetMapping("/dashboard/invite_url")
	public String link_invite_family() {
		// 가족 초대자 가입뷰로 이동. 기본 이메일 채워서 설정
		// 가입 버튼 누르면 바로 그룹으로 생성할지 논의할 것
		return "";
	}

	// 육아일지 작성
	@GetMapping("/dashboard/babyBook/write")
	public String write_diary() {
		return "";
	}

	// 육아일기 작성
	@ResponseBody
	@PostMapping("/dashboard/babyBook/write")
	public Infant_diary_response write_diary_post(@RequestParam(value = "images", required = false) MultipartFile[] attached_file, @RequestParam("title") String title,
			@RequestParam("update_contents") String contents, @RequestParam(value="share", required=false) String share,
			@RequestParam("event_date") String event_date, @RequestParam(name = "hashtag", required = false) String hashtag, HttpSession session) throws IOException {
		// 입력값 검증. 해쉬태그 갯수 제한, 특수문자 사용 제한, 파일이 첨부되었으면 첨부파일 갯수 제한
		// board DB insert. 공개여부 설정 (전체공개/비공개/그룹공개), category = '3'(갤러리)
		// shared값은 true(1) / false(0). shared가 true면 갤러리에 같이 공유
		String SHARE_TRUE_VALUE = "1";

		Infant_diary_response response = new Infant_diary_response();

		if(event_date == null || event_date.length() == 0) {
			response.setResult("db");
			return response;
		}

		if(title == null || title.trim().length() == 0) {
			response.setResult("title_required");
			return response;
		}

		if(contents == null || contents.trim().length() == 0) {
			response.setResult("contents_required");
			return response;
		}

		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");
		Integer group_idx = (Integer) session.getAttribute("group_idx");

		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		// 그룹이 아니면 infant_idx와  member_idx가 관계테이블에 있는지 체크
		// 그룹이면 그룹장의 group_admin_idxdhk infant_idx가 관계테이블에 있는지 체크
		if(group_idx != null) {
			Member_infant mi = groupService.findMI(info.getGroup_admin_idx(), infant_idx);
			if(mi == null) { 
				response.setResult("no_infant_auth");
				return response;
			}
		} else {
			Member_infant mi = groupService.findMI(member_idx, infant_idx);
			if(mi == null) { 
				response.setResult("no_infant_auth");
				return response;
			}
		}


		boolean upload_chk = false;
		List<Id_file> files = null;

		// 파일이 첨부되었으면 board_files insert
		if (attached_file != null && attached_file.length > 0) {
			upload_chk = true;
			files = new ArrayList<Id_file>();

			for (int i = 0; i < attached_file.length; i++) {
				if (attached_file[i].getSize() <= 0) {
					continue;
				}
				if (i == 4) {
					break;
				}

				String original_file_name = attached_file[i].getOriginalFilename();
				if (memberService.checkFileName(original_file_name) == false) {
					response.setResult("file_name_error");
					return response;
				}    // 파일명 에러
				if (attached_file[i].getSize() > 3000000) {
					response.setResult("file_size_error");
					return response;
				}    // 파일 크기 에러(3MB)

				if(original_file_name.getBytes().length > 50) {
					response.setResult("file_name_size_error");
					return response;
				}

				if(s3Service.checkBucket("dashboard/diary/" + member_idx) == false) {
					s3Service.createBucket("dashboard/diary/" + member_idx);
				}

				String server_filename = memberService.getUniqFileName(original_file_name);
				int tmp = original_file_name.indexOf('.');
				String extension = original_file_name.substring(tmp + 1, original_file_name.length());    // 파일 확장자

				InputStream inputStream = null;
				BufferedImage originalImage = null;

				Id_file id_file = new Id_file();

				if(share != null && share.equals(SHARE_TRUE_VALUE)) {		// 공유하기 클릭했을시
					if(i == 0) {
						id_file.setRepresent('1');	// 첫번째 첨부파일을 대표이미지로 설정한다.
					} else {
						id_file.setRepresent('0');
					}
				} else {	// 공유하기를 하지 않으면 모든 이미지가 대표이미지가 아님으로 설정된다.
					id_file.setRepresent('0');
				}

				try {
					inputStream = attached_file[i].getInputStream();
					originalImage = ImageIO.read(inputStream);
					// 원본 이미지 업로드
					s3Service.putThumbnail("dashboard/diary/" + member_idx, server_filename, attached_file[i].getInputStream(), s3Service.getMetadata(attached_file[i]));

					// 썸네일 업로드 (첫번째 이미지만 썸네일 생성)
					if(share != null && share.equals(SHARE_TRUE_VALUE) && i == 0) {	
						set_thumbnail(originalImage, server_filename, extension);
					}	

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						inputStream.close();
						inputStream = null;
					}
					if (originalImage != null) {
						originalImage.flush();
						originalImage = null;
					}
				}

				id_file.setFile_url("https://s3.ap-northeast-2.amazonaws.com/littleone/dashboard/diary/" + member_idx + "/" + server_filename);
				id_file.setOriginal_filename(attached_file[i].getOriginalFilename());
				id_file.setServer_filename(server_filename);

				files.add(id_file);
			}
		}	// 업로드 파일 처리 end


		// 다이어리 db insert
		Infant_diary diary = new Infant_diary();
		diary.setSubject(title);
		diary.setContents(contents);    // html 파싱 필요
		diary.setInfant(infantService.findOne(infant_idx));
		diary.setMember(memberService.findByIdx(member_idx));

		if(share != null && share.equals(SHARE_TRUE_VALUE) && upload_chk == true) {
			// 파일첨부가 되었고 공유하기를 체크했을 시
			// attached_file[0]을 대표이미지로 설정
			diary.setShare('1');
		} else {
			diary.setShare('0');
		}

		diary.setEvent_date(event_date);
		diary.setDate_created(memberService.set_now_time());
		diary.setHits(0);
		diary.setLikes(0);

		if(hashtag != null && hashtag.length() > 1) {
			diary.setHashtag(hashtag);
		}

		diary = dashboardService.save(diary);	// transaction

		if (diary == null) {
			response.setResult("db");
			return response;
		}

		if(upload_chk) {
			for(int i=0 ; i<files.size() ; i++) {
				files.get(i).setDiary_idx(diary.getIdx());
				dashboardService.saveIdFile(files.get(i));
				diary.getId_files().add(files.get(i));
			}
		}

		// 그룹에게 알림 전송 (dashboardInterceptor 들린 후 적용됨)
		if (info != null && info.getGroup_members() != null) {
			for (int i = 0; i < info.getGroup_members().size(); i++) {
				int idx = info.getGroup_members().get(i).getIdx();
				if (idx == member_idx) {
					continue;
				}
				Alert alert = new Alert();
				alert.setRequester(member_idx);
				alert.setRecipient(idx);
				alert.setRequest_date(memberService.set_now_time());
				alert.setEvent_type('4');
				alert = alarmService.insert(alert);
				if (alert == null) {
					response.setResult("db");
					return response;
				}
			}
		}

		List<Infant_diary> diary_list = dashboardService.findByDateDiary(infant_idx, event_date);

		if(diary_list.size() > 0) {	//substring(11, 16)
			String date_created = diary_list.get(0).getDate_created();
			if(date_created.length() != 5) {
				diary_list.get(0).setDate_created(date_created.substring(11, 16));
			}	
		}

		response.setResult("success");
		response.setDiary_list(diary_list);

		return response;
	}

	// 육아일기 글목록
	@GetMapping("/dashboard/babybook/list_view")
	public String babybook_list(HttpSession session, Model model) {
		// member_idx 기준 다이어리 게시글 리스트 찾아서 model에 추가
		// 육아일기 리스트는 글위주로 보여줄지 이미지 위주로 보여줄지 기획 필요. 이미지 첨부안될수도 있음
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		model.addAttribute("group_admin_idx", info.getGroup_admin_idx());
		return "dashboard/babybook/index";
	}

	// 대쉬보드 캘린더에서 특정 날짜(일) 클릭시 육아일기, 육아일정 글목록
	@ResponseBody
	@PostMapping("/dashboard/babybook/list_view_byDate")
	public BabyBook babybook_list_byDate(@RequestParam("date") String date, HttpSession session) {	// date = yyyy-mm-dd
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		// member_idx 기준 다이어리 게시글 리스트 찾아서 model에 추가
		// 육아일기 리스트는 글위주로 보여줄지 이미지 위주로 보여줄지 기획 필요. 이미지 첨부안될수도 있음
		//String date2 = date.replace('.', '-').replaceAll("\\p{Z}", "");		// 공백제거

		String date1 = date.substring(0, 7);	// yyyy-mm
		String date2 = date.substring(8, 10);	// dd

		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			return null;	// 아이정보 없음
		}

		List<Infant_diary> diary_list = null;
		List<Infant_schedule> schedule_list = null;

		Member_infant mi = null;

		Integer group_idx = (Integer) session.getAttribute("group_idx");

		// 그룹 및 아기 검증
		if (group_idx == null && info != null) {
			// 유저가 그룹에 속하지 않았으면
			mi = groupService.findMI(member_idx, infant_idx);
			if(mi == null) {
				return null;
			}
		} else {
			// 유저가 그룹에 속했으면
			mi = groupService.findMI(info.getGroup_admin_idx(), infant_idx);	// null exception
			if(mi == null) {
				return null;
			}

			//diary_list = dashboardService.findByDateDiary_byGroup(infant_idx, info.getGroup_members(), date2);                        // 다이어리는 그룹장만 작성가능하므로 그룹장 idx를 넘겨줌
			diary_list = dashboardService.findByDateDiary(infant_idx, date);
			//schedule_list = dashboardService.findByDateSchedule_byGroup(infant_idx, info.getGroup_members(), date2);    // 스케쥴은 그룹멤버 모두가 작성가능하므로 전체 그룹 멤버 idx를 넘겨줌
			schedule_list = dashboardService.findByDateSchedule(infant_idx, date1, date2);
		}


		BabyBook babybook = null;
		if (diary_list != null && diary_list.size() > 0) {
			babybook = new BabyBook();
			babybook.setDiary_list(diary_list);
		}
		if (schedule_list != null && schedule_list.size() > 0) {
			if (babybook == null) {
				babybook = new BabyBook();
			}
			babybook.setSchedule_list(schedule_list);
		}

		return babybook;
	}

	public List<Infant_diary> diaryList_byDate_index(String date, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		// member_idx 기준 다이어리 게시글 리스트 찾아서 model에 추가
		// 육아일기 리스트는 글위주로 보여줄지 이미지 위주로 보여줄지 기획 필요. 이미지 첨부안될수도 있음
		//String date2 = date.replace('.', '-').replaceAll("\\p{Z}", "");		// 공백제거
		String date2 = date.substring(0, 10);

		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			return null;
		}

		List<Infant_diary> diary_list = null;
		Member_infant mi = null;
		if (info.getGroup_idx() == -1) {
			// 유저가 그룹에 속하지 않았으면
			mi = groupService.findMI(member_idx, infant_idx);
		} else {
			// 유저가 그룹에 속했으면
			mi = groupService.findMI(info.getGroup_admin_idx(), infant_idx);
		}

		if(mi == null) { return null; }

		diary_list = dashboardService.findByDateDiary(infant_idx, date2);

		return diary_list;

	}

	public List<Infant_schedule> scheduleList_byDate_index(String date, HttpSession session) {	// yyyy-mm-dd
		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		// member_idx 기준 다이어리 게시글 리스트 찾아서 model에 추가
		// 육아일기 리스트는 글위주로 보여줄지 이미지 위주로 보여줄지 기획 필요. 이미지 첨부안될수도 있음
		//String date2 = date.replace('.', '-').replaceAll("\\p{Z}", "");		// 공백제거

		String date1 = date.substring(0, 7);	// yyyy-mm
		String date2 = date.substring(8, 10);	// dd

		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			return null;
		}

		List<Infant_schedule> schedule_list = null;

		if (info.getGroup_idx() == -1) {
			// 유저가 그룹에 속하지 않았으면
			schedule_list = dashboardService.findByDateSchedule(infant_idx, date1, date2);
		} else {
			// 유저가 그룹에 속했으면
			//schedule_list = dashboardService.findByDateSchedule_byGroup(infant_idx, info.getGroup_members(), date2);    // 스케쥴은 그룹멤버 모두가 작성가능하므로 전체 그룹 멤버 idx를 넘겨줌
			schedule_list = dashboardService.findByDateSchedule(infant_idx, date1, date2);
		}

		return schedule_list;	
	}

	// 대쉬보드 캘린더에 데이터가 있을경우 일별 점찍기
	@ResponseBody
	@PostMapping("/dashboard/babybook/checkByMonth")
	public List<DiaryResponse> babybook_checkByMonth(@RequestParam("date") String date, HttpSession session) {        // date 형식 : 2018-07 (월까지)
		//select substring(event_date, 9, 2) from infant_schedule where infant_idx = 20 AND member_idx = 55 AND substring(event_date, 1,7) = '2018-07';
		//select substring(event_date, 9, 2) from infant_diary where infant_idx = 20 AND member_idx = 55 AND substring(event_date, 1,7) = '2018-07';
		if (session.getAttribute("infant_idx") == null) {
			return null;
		}

		BabybookResponse response = null;
		List<ScheduleResponse> schedule_response = null;
		List<DiaryResponse> diary_response = null;

		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			return null;
		}

		Integer group_idx = (Integer) session.getAttribute("group_idx");

		if (group_idx != null) {
			// 유저가 그룹에 속했으면
			schedule_response = dashboardService.checkByDateSchedule(infant_idx, member_idx, date);
			diary_response = dashboardService.checkByDateDiary(infant_idx, member_idx, date);
		} else {
			// 유저가 그룹에 속하지 않았으면
			schedule_response = dashboardService.checkByDateSchedule(infant_idx, member_idx, date);
			diary_response = dashboardService.checkByDateDiary(infant_idx, member_idx, date);
		}

		if(schedule_response != null && schedule_response.size() > 0) {
			response = new BabybookResponse();
			response.setSchedule_response(schedule_response);
		} 

		if(diary_response != null && diary_response.size() > 0) {
			if(response == null) { response = new BabybookResponse(); }
			response.setDiary_response(diary_response);

		}

		Set<Integer> schedule_day = new LinkedHashSet<Integer>();

		for(int i=0 ; i<schedule_response.size() ; i++) {
			int index = Integer.parseInt(schedule_response.get(i).getEvent_date_start());
			int index2 = Integer.parseInt(schedule_response.get(i).getEvent_date_end());

			for(int j=index ; j <= index2 ; j++) {
				schedule_day.add(j);
			}
		}

		Set<Integer> diary_day = new LinkedHashSet<Integer>();

		for(int i=0 ; i<diary_response.size() ; i++) {
			int index = Integer.parseInt(diary_response.get(i).getEvent_date_start());	
			diary_day.add(index);
		}

		List<DiaryResponse> results = new ArrayList<DiaryResponse>();

		for(int i=0 ; i<31 ; i++) {
			boolean chk = false;
			DiaryResponse e = null;
			if(diary_day.contains(i)) {
				e = new DiaryResponse();
				e.setEvent_date_start(i + "");
				chk = true;
			}

			if(schedule_day.contains(i)) {
				if(chk) {
					// 위 e 객체에 event_type에 ",2"를 append하고 results.add
					e.setEvent_type(e.getEvent_type() + ",2");
					results.add(e);
					chk = false;
				} else {
					e = new DiaryResponse();
					e.setEvent_date_start(i + "");
					e.setEvent_type("2");
					results.add(e);
				}
			}

			if(chk) {
				results.add(e);
			}

		}

		return results;
	}

	@ResponseBody
	@PostMapping("/dashboard/babySchedule/checkByMonth")
	public List<ScheduleResponse> babySchedule_checkByMonth(@RequestParam("date") String date, HttpSession session) {        // date 형식 : 2018-07 (월까지)
		//select substring(event_date, 9, 2) from infant_schedule where infant_idx = 20 AND member_idx = 55 AND substring(event_date, 1,7) = '2018-07';
		//select substring(event_date, 9, 2) from infant_diary where infant_idx = 20 AND member_idx = 55 AND substring(event_date, 1,7) = '2018-07';

		List<ScheduleResponse> response = new ArrayList<ScheduleResponse>();
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			return null;
		}

		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		Integer group_idx = (Integer) session.getAttribute("group_idx");

		if (group_idx != null) {
			// 유저가 그룹에 속했으면
			List<Member> group_members = info.getGroup_members();
			response = dashboardService.checkByDateScheduleByGroup(infant_idx, group_members, date);
			//day_diary = dashboardService.checkByDateDiaryByGroup(infant_idx, info.getGroup_members(), date);
		} else {
			// 유저가 그룹에 속하지 않았으면
			//day_schedule = dashboardService.checkByDateSchedule(infant_idx, member_idx, date);
			//day_diary = dashboardService.checkByDateDiary(infant_idx, member_idx, date);
		}

		/*if(day_schedule != null) {
			return day_schedule;
		} else {
			return day_diary;
		}*/
		// 임시로 다이어리 작성 날짜만 전송
		return response;
	}

	// 대쉬보드 아기 일정 생성
	@ResponseBody
	@PostMapping("/dashboard/add_schedule")
	public Infant_schedule_response add_schedule(@RequestParam(value="title", required=false) String title, @RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date, @RequestParam("event_type") char event_type, HttpSession session) {

		Infant_schedule_response response = new Infant_schedule_response();

		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		int member_idx = (int) session.getAttribute("idx");
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		//2018/07/31/ 10:41 am
		//2018-07-31 10:41
		//2018/07/31/ 10:41 pm (pm 일경우 h + 12)
		//2018-07-31 22:41
		// 마지막 초의 경우 m 뒤에 :00 붙일것

		String start2 = null;
		String end2 = null;

		if(start_date.contains("pm")) {
			int hour = Integer.parseInt(start_date.substring(11, 13)) + 12;
			start2 = start_date.substring(0, 11) + hour + start_date.substring(13, 16) + ":00";
		} else {
			start2 = start_date.substring(0, 16) + ":00";
		}

		if(end_date.contains("pm")) {
			int hour = Integer.parseInt(end_date.substring(11, 13)) + 12;
			end2 = end_date.substring(0, 11) + hour + end_date.substring(13, 16) + ":00";
		} else {
			end2 = end_date.substring(0, 16) + ":00";
		}

		Infant_schedule schedule = new Infant_schedule(infantService.findOne(infant_idx), memberService.findByIdx(member_idx), start2, end2, memberService.set_now_time(), null, title, event_type);
		schedule = dashboardService.saveIdSchedule(schedule);

		if (schedule == null) {
			response.setResult("db_error");
			return response;
		}

		Integer group_idx = (Integer) session.getAttribute("group_idx");
		if (group_idx != null) {
			//List<Member_group> list = groupService.findByGroup_idx(group.getGroup_idx());
			for (int i = 0; i < info.getGroup_members().size(); i++) {
				int idx = info.getGroup_members().get(i).getIdx();
				if (idx == member_idx) {
					continue;
				}
				Alert alert = new Alert();
				alert.setRequester(member_idx);
				alert.setRecipient(idx);
				alert.setRequest_date(memberService.set_now_time());
				alert.setEvent_type('5');
				alert = alarmService.insert(alert);
				if (alert == null) {
					response.setResult("db_error");
					return response;
				}
			}
		}

		List<Infant_schedule> schedule_list = dashboardService.findByDate(infant_idx, info.getDate());
		if(schedule_list.size() > 0) {
			schedule_list.get(0).setDate_created(schedule_list.get(0).getDate_created().substring(11, 16));
		}

		response.setSchedule_list(schedule_list);
		response.setResult("success");

		return response;
	}

	@ResponseBody
	@GetMapping("/dashboard/delete_schedule/{schedule_idx}/{selected_date}")
	public Infant_schedule_response delete_schedule(@PathVariable("schedule_idx") int schedule_idx, @PathVariable("selected_date") String selected_date, HttpSession session) {

		Infant_schedule_response response = new Infant_schedule_response();
		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");
		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		Infant_schedule is = dashboardService.findSchedule(schedule_idx);
		if (member_idx != is.getMember().getIdx()) {
			response.setResult("no_authority");
			return response;
		}
		dashboardService.delete_schedule(schedule_idx);


		List<Infant_schedule> schedule_list = dashboardService.findByDate(infant_idx, selected_date);
		response.setResult("success");
		response.setSchedule_list(schedule_list);

		return response;
	}

	@ResponseBody
	@GetMapping("/dashboard/delete_diary/{diary_idx}/{selected_date}")	
	public Infant_diary_response delete_diary(@PathVariable("diary_idx") int diary_idx, @PathVariable("selected_date") String selected_date, HttpSession session) {
		//selected_date : yyyy-mm-dd hh:mm:ss

		Infant_diary_response response = new Infant_diary_response();
		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");
		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		Integer group_idx = (Integer) session.getAttribute("group_idx");

		Infant_diary is = dashboardService.findDiary(diary_idx);
		if (member_idx != is.getMember().getIdx()) {
			return response;
		}
		dashboardService.delete_diary(diary_idx);

		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");

		List<Infant_diary> diary_list = null;
		Member_infant mi = null;
		if(group_idx == null) {
			mi = groupService.findMI(member_idx, infant_idx);
		} else {
			mi = groupService.findMI(info.getGroup_admin_idx(), infant_idx);
		}

		if(mi == null) {
			response.setResult("no_authoritiy_infant");
			return response;
		}

		diary_list = dashboardService.findByDateDiary(infant_idx, selected_date.substring(0, 10));	// yyyy-mm-dd
		response.setResult("success");

		if(diary_list != null && !diary_list.isEmpty()) {
			response.setDiary_list(diary_list);
		}

		return response;

	}

	@ResponseBody
	@GetMapping("/dashboard/get_diary/{diary_idx}")
	public Infant_diary get_diary(@PathVariable("diary_idx") int diary_idx, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		Infant_diary is = dashboardService.findDiary(diary_idx);
		Integer group_idx = (Integer) session.getAttribute("group_idx");

		int writer_idx = is.getMember().getIdx();

		if (group_idx == null && member_idx != writer_idx) {
			return null;
		} else {
			Member_group mg = groupService.findMember_group(writer_idx);
			if(mg != null) {
				return is;
			} else {
				return null;
			}

		}
	}


	// 다이어리 수정시 이미지 새로 업로드 할때마다 S3 서버 임시디렉토리에 저장해두는 코드. 저장후 이미지 경로 리턴
	@ResponseBody
	@PostMapping("/dashboard/babyBook/modify_upload")
	public String upload_diary_image(@RequestParam("image") MultipartFile image, HttpSession session) {
		// 파일명 검증, 확장자 검증 필요
		int member_idx = (int) session.getAttribute("idx");
		String original_filename = image.getOriginalFilename();
		String server_name = memberService.getUniqFileName(original_filename);
		InputStream input = null;

		Integer infant_idx = (Integer) session.getAttribute("infant_idx");
		if(infant_idx == null) {
			return "no_infant";
		}

		try {
			input = image.getInputStream();

			// temp 디렉토리가 없으면 temp 디렉토리 생성
			if(s3Service.checkBucket("dashboard/diary/" + member_idx + "/temp") == false) {
				s3Service.createBucket("dashboard/diary/" + member_idx + "/temp");
			}
			// s3 temp 디렉토리에 이미지 업로드
			s3Service.putThumbnail("dashboard/diary/" + member_idx + "/temp", server_name, input, s3Service.getMetadata(image));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "https://s3.ap-northeast-2.amazonaws.com/littleone/dashboard/diary/" + member_idx + "/temp/" + server_name;
	}


	@ResponseBody
	@PostMapping(value="/dashboard/modify_diary/{diary_idx}")
	public Infant_diary_response modify_diary_post(@PathVariable("diary_idx") int diary_idx, //@RequestParam("update_date") String update_date,
			@RequestParam("update_title") String update_title, @RequestParam("update_contents") String update_contents,
			@RequestParam(value="update_hashtag", required=false) String update_hashtag,
			@RequestParam(value="update_share", required=false) char update_share,
			@RequestParam(value="update_file", required=false) String update_file,	// 기존 첨부파일 url이 들어옴. 만약 삭제되었으면 url에서 제거됨
			@RequestParam(value="uploadModifyChk") boolean uploadModifyChk,
			@RequestParam(value="selected_date", required=true) String date,	// yyyy-mm-dd hh:mm:ss
			HttpSession session) throws IOException {

		Infant_diary_response response = new Infant_diary_response();
		Infant_diary id = dashboardService.findDiary(diary_idx);
		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		if (member_idx != id.getMember().getIdx()) {
			response.setResult("no_authority");
			return response;
		}

		if(update_share != '1' && update_share != '0') {
			response.setResult("share_value_error");
			return response;
		}

		// infant_diary update (share 추가 필요)
		int result = dashboardService.updateDiary(diary_idx, update_title, update_contents, memberService.set_now_time(), update_hashtag, update_share);
		if (result != 1) {
			// DB rollback
			response.setResult("db_error");
			return response;
		}

		// id_files 기존 행 삭제 및 aws s3 기존파일 삭제. 프론트에서 삭제된 server_filename과 id_file idx값 필요	
		if (uploadModifyChk == true && update_file != null && update_file.length() > 0) {

			dashboardService.deleteFileByDiary_idx(diary_idx);	// 기존에 저장된 모든 id_file db 삭제

			//List<DiaryModifyFile> files = new ArrayList<DiaryModifyFile>();		// 수정시 첨부로 여겨진 기존 파일들 idx
			//List<Id_file> origin_files = dashboardService.findIdFileByDiary_idx(diary_idx);	// 해당 게시글의 과거 파일들 idx
			Gson gson = new Gson();
			String[] file_request = update_file.split("},");

			for(int i=0 ; i<file_request.length ; i++) {
				if(file_request[i].charAt(file_request[i].length()-1) != '}') {
					file_request[i] += "}";
				}

				DiaryModifyFile target = gson.fromJson(file_request[i], DiaryModifyFile.class);
				String s3_url = target.getFile_url();
				int index = s3_url.lastIndexOf('/');
				String server_name = s3_url.substring(index + 1);
				String extension = server_name.substring(server_name.indexOf('.') + 1);

				if(target.getIdx() == -1) {	// 새로 업로드된 이미지면
					// 2. S3 객체 복사 및 temp 파일 삭제
					s3Service.copyObject("dashboard/diary/" + member_idx + "/temp", server_name, "dashboard/diary/" + member_idx, server_name);
					s3Service.deleteThumbnail("dashboard/diary/" + member_idx + "/temp", server_name);
				} 

				// 3. Id_file DB insert (파일 순서 고려 필요. 삭제된 파일은 삭제 처리, 추가된 파일은 추가처리)
				Id_file id_file = new Id_file(diary_idx, server_name, server_name, "https://s3.ap-northeast-2.amazonaws.com/littleone/dashboard/diary/" + member_idx + "/" + server_name);

				if(i == 0) {
					id_file.setRepresent('1');
					// 썸네일 s3 insert
					InputStream input;
					BufferedImage thumb;

					input = s3Service.getInputStream("dashboard/diary/" + member_idx, server_name);
					thumb = ImageIO.read(input);
					set_thumbnail(thumb, server_name, extension);

				}

				dashboardService.saveIdFile(id_file);
			}
		}

		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");
		List<Infant_diary> diary_list = null;
		Member_infant mi = null;
		if(info.getGroup_idx() != -1) {
			mi = groupService.findMI(info.getGroup_admin_idx(), infant_idx);
		} else {
			mi = groupService.findMI(member_idx, infant_idx);
		}

		if(mi == null) {
			response.setResult("no_authority");
			return response;
		}

		if(date.length() > 10) {
			date = date.substring(0, 10);
		}
		diary_list = dashboardService.findByDateDiary(infant_idx, date);
		response.setDiary_list(diary_list);
		response.setResult("success");
		return response;
	}

	@ResponseBody
	@GetMapping("/dashboard/get_schedule/{schedule_idx}")
	public Infant_schedule get_schedule(@PathVariable("schedule_idx") int schedule_idx, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		Integer group_idx = (Integer) session.getAttribute("group_idx");
		Infant_schedule is = dashboardService.findSchedule(schedule_idx);

		int writer_idx = is.getMember().getIdx();
		if (group_idx == null && member_idx != writer_idx) {
			return null;
		} else {
			Member_group mg = groupService.findMember_group(writer_idx);
			if(mg != null) {
				return is;
			} else {
				return null;
			}

		}
	}

	@ResponseBody
	@PostMapping("/dashboard/modify_schedule/{schedule_idx}")
	public Infant_schedule_response modify_schedule_post(@PathVariable("schedule_idx") int schedule_idx, @RequestParam("update_start_date") String update_start_date,
			@RequestParam("update_end_date") String update_end_date, @RequestParam("update_title") String update_title,
			@RequestParam("selected_date") String selected_date, HttpSession session) {

		Infant_schedule_response response = new Infant_schedule_response();
		int member_idx = (int) session.getAttribute("idx");
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");

		if(infant_idx == null) {
			response.setResult("no_infant");
			return response;
		}

		Infant_schedule is = dashboardService.findSchedule(schedule_idx);
		if (member_idx != is.getMember().getIdx()) {
			response.setResult("no_authority");
			return response;
		} else {
			int result = dashboardService.updateSchedule(schedule_idx, update_start_date, update_end_date, update_title);
			if (result == 1) {
				List<Infant_schedule> schedule_list = dashboardService.findByDate(infant_idx, selected_date);
				response.setSchedule_list(schedule_list);
				response.setResult("success");
				return response;
			} else {
				// db rollback
				response.setResult("failed");
				return response;
			}

		}

	}

	@ResponseBody
	@GetMapping("/dashboard/getDashboardWidget/{member_idx}")
	public JsonDashboard[] getDashboardWidget(@PathVariable("member_idx") int member_idx) {
		Dashboard d =  dashboardService.findDashboard(member_idx);
		if(d != null) {
			/*1 : bottle
			2 : peepee
			3 : infant_info
			4 : temp
			5 : diary
			6 : group_info
			 */

			//List<JsonDashboard> list = new ArrayList<JsonDashboard>();
			String param[] = {d.getTemp() + "-4", d.getPeepee() + "-2", d.getBottle() + "-1", /*d.getDiary() + "-5",*/ d.getInfant_info() + "-3", d.getGroup_info() + "-6"};
			JsonDashboard[] list = new JsonDashboard[param.length];

			for(int i=0 ; i<param.length ; i++) {
				String temp = param[i];
				if(temp != null && !temp.contains("null")) {
					JsonDashboard board = new JsonDashboard();
					board.setAttr(temp.split("-")[2].charAt(0));
					board.setX(Double.parseDouble(temp.split("-")[1]));
					int index = Integer.parseInt(temp.split("-")[0]);
					list[index] = board;
				}
			}
			return list;
		} else {
			return null;
		}
	}

	@ResponseBody
	@PostMapping("/dashboard/saveDashboardWidget")
	public String saveDashboardWidget(@RequestBody List<JsonDashboard> widget_list, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		Dashboard dashboard = dashboardService.findDashboard(member_idx);
		if(dashboard == null) {
			dashboard = new Dashboard();
			dashboard.setMember_idx(member_idx);
			dashboard.setDashboardByJson(widget_list);
			dashboard = dashboardService.saveDashboard(dashboard);
			if(dashboard == null) {
				return "failed";
			}
		} else {
			dashboard = new Dashboard();
			dashboard.setDashboardByJson(widget_list);
			int result = dashboardService.updateDashboard(member_idx, dashboard.getTemp(), dashboard.getPeepee(), dashboard.getBottle(), /*dashboard.getDiary(),*/
					dashboard.getInfant_info(), dashboard.getGroup_info());
			if(result != 1) {
				return "failed";
			}
		}

		return "success";
	}

	public void set_thumbnail(BufferedImage originalImage, String server_filename, String extension) {
		// 썸네일 생성하여 같이 올림.
		BufferedImage thumbnail = null;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		try {
			thumbnail = Thumbnails.of(originalImage).size(365, ((365*height)/width)).outputQuality(1.0f).asBufferedImage();
			s3Service.putThumbnailBI("community/gallery/gallery_thumbnail", server_filename, extension, thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 대시보드 메인페이지 이동시 호출되는 함수
	public void dashboard_index_setting(String setting_date, HttpSession session, Model model) {
		int member_idx = (int) session.getAttribute("idx");
		// 그룹 idx설정할지 member_idx 설정할지 결정할것
		GroupInfo info = (GroupInfo) session.getAttribute("dashboard_info");

		if(info != null && info.getShared_infant() != null) {	//아기정보가 등록되어있는경우

			String infant_idx = Integer.toString(info.getShared_infant().getIdx());
			String date, parse_date;
			if(setting_date != null) {
				date = setting_date;
				parse_date = date.substring(0, 4) + date.substring(5,7) + date.substring(8);

			} else {
				SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
				parse_date = date_format.format(new Date());
			}

			int feed_cnt = dbService.readBottleCountByDate(infant_idx, parse_date);
			info.setFeed_cnt(feed_cnt);

			int defec_cnt = dbService.readPeepeeCountByDate(infant_idx, parse_date);
			info.setDefec_cnt(defec_cnt);

			if(feed_cnt > 0) {
				String bottle_time = dbService.getRecentBottle(Integer.parseInt(infant_idx), parse_date);
				if(bottle_time != null) {
					info.setBottle_time(bottle_time.substring(8, 10) + ":" + bottle_time.substring(10, 12) + ":" + bottle_time.substring(12));
				} 
			} else {
				info.setBottle_time(null);
			}

			if(defec_cnt > 0) {
				String peepee_time = dbService.getRecentPeepee(Integer.parseInt(infant_idx), parse_date);
				if(peepee_time != null) {
					info.setPeepee_time(peepee_time.substring(8, 10) + ":" + peepee_time.substring(10, 12) + ":" + peepee_time.substring(12));
				}
			} else {
				info.setPeepee_time(null);
			}

			String RecentTempAndDate = dbService.getRecentTemp(Integer.parseInt(infant_idx), parse_date);
			if(RecentTempAndDate.split("-")[0].equals("null") == false) {
				String temp = RecentTempAndDate.split("-")[0];
				if(temp.contains(".") == false) {
					temp = temp + ".0";
				}

				info.setLast_temp(temp);
				String temp_time = RecentTempAndDate.split("-")[1];
				info.setTemp_time(temp_time.substring(8, 10) + ":" + temp_time.substring(10, 12) + ":" + temp_time.substring(12));
			} else {
				info.setLast_temp(null);
				info.setTemp_time(null);
			}


			model.addAttribute("defec_cnt", info.getDefec_cnt());
			model.addAttribute("feed_cnt", info.getFeed_cnt());

			if(info.getBottle_time() != null) {
				model.addAttribute("bottle_time", info.getBottle_time());
			}
			if(info.getPeepee_time() != null) {
				model.addAttribute("peepee_time", info.getPeepee_time());
			}

			if(info.getLast_temp() != null) {
				model.addAttribute("last_temp", info.getLast_temp());
			}
			if(info.getTemp_time() != null) {
				model.addAttribute("temp_time", info.getTemp_time());
			}
			String checkDiary = dashboardService.checkDiary(info.getShared_infant().getIdx());		// 최근 작성일을 가져옴. 없으면 null
			int checkSchedule = dashboardService.checkSchedule(info.getShared_infant().getIdx(), member_idx);
			if (checkDiary != null) {
				model.addAttribute("diary_list_by_Member", checkDiary);
			}
			if (checkSchedule >= 1) {
				model.addAttribute("schedule_list_by_Member", "true");
			}

			Device_response res = deviceService.findByMemberResponse(member_idx);
			if(res != null) {
				model.addAttribute("device_list", res);
			}

			model.addAttribute("infant", info.getShared_infant());    // 아이가 한명 이상일 경우
			model.addAttribute("shared_infant", info.getShared_infant());

			Infant_thumbnail it = dashboardService.findInfantThumbnail(info.getShared_infant().getIdx());
			if(it != null) {
				model.addAttribute("shared_infant_thumbnail", "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/infant/" + it.getServer_file());
			}

			if(info.getGroup_members() != null && info.getGroup_members().size() > 0) {	// 그룹이 존재할 경우
				model.addAttribute("family_group", info.getGroup_members());
				model.addAttribute("group_admin", info.getGroup_admin());
				model.addAttribute("group_admin_idx", info.getGroup_admin_idx());
				model.addAttribute("group_idx", info.getGroup_idx());
			}

			session.setAttribute("infant_idx",  info.getShared_infant().getIdx());

			List<Infant_diary> diary_list = null;
			List<Infant_schedule> schedule_list = null;

			diary_list = diaryList_byDate_index(setting_date, session);
			schedule_list = scheduleList_byDate_index(setting_date, session);

			if(diary_list != null && diary_list.size() > 0) {
				model.addAttribute("diary_list", diary_list);
			}
			if(schedule_list != null && schedule_list.size() > 0) {
				model.addAttribute("schedule_list", schedule_list);
			}

			model.addAttribute("selected_date", setting_date);


		} else {	// info가 없을경우 (아이정보가 없을경우)
			model.addAttribute("selected_date", memberService.set_now_date());
		}
	}
}
