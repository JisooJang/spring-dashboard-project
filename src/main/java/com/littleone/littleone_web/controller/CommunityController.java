package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Alert_board;
import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Board_file;
import com.littleone.littleone_web.domain.Board_index;
import com.littleone.littleone_web.domain.Board_likes;
import com.littleone.littleone_web.domain.Comment;
import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Comment_data;
import com.littleone.littleone_web.domain.Comment_likes;
import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.TmpFileInfo;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.BoardService;
import com.littleone.littleone_web.service.GalleryService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import software.amazon.ion.IonException;

@Controller
public class CommunityController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberMngService mngService;
    
    @Autowired
    private InfantService infantService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private BoardService boardService;

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private AlertService alarmService;

    private int search_result_count = 0;

    private int page_num = 1;

    
    // aws s3 bucket expiration day 설정 테스트
    @ResponseBody
	@GetMapping("/test/s3lifecycle")
	public String test2() {
    	amazonS3Service.setBucketLifeCycle("littleone/dashboard/diary/55/temp");
		return "success";
	}
    
    @ResponseBody
    @GetMapping("/removeTempfile/{service_name}")
    public String remove_tmp_file_bySession(@PathVariable("service_name") String service_name, HttpSession session) {
        //1. S3의 sevice에 따른 tmp 버킷에서 세션에있는 tmp_file을 지운다.
        memberService.removeTmpFileS3(service_name, session);

        // 2. 세션에 저장해둔 임시 파일 이름들을 지운다.
        return memberService.removeSessionTempFile(service_name, session);
    }

    @GetMapping("/community")
    public String get_community() {
        return "community/community_index";
    }

    @GetMapping("/community/product/view")
    public String community_product_view() {
        return "community/product/view";
    }

    // qna 게시판 글 목록(페이징 처리 - 최신순)
    @GetMapping("/community/{category}/page/{page_index}")
    public String view_qna_board_paging(@PathVariable("category") String category, @PathVariable("page_index") int page_index, 
    		Model model, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "keyword", required = false) String keyword) throws ParseException {
        int i = 0;
        if (!category.equals("qna") && !category.equals("service")) {
            System.out.println("올바르지 않은 커뮤니티 카테고리");
            return "error";
        }
        List<Integer> page_list = new ArrayList<Integer>();

        if (category.equals("qna")) {
            model.addAttribute("type", "qna");
            List<Board_index> qna_board = boardService.getQnaBoardPaging(page_index);
            List<Board_index> qna_board_notice = boardService.getQnaBoardNotice();

            qna_board = boardService.setBoardIndex(qna_board);
            qna_board_notice = boardService.setBoardIndex(qna_board_notice);

            model.addAttribute("user_board", qna_board);        // 일반 게시글
            model.addAttribute("notice_board", qna_board_notice);    // 공지 게시글

            int size = boardService.getCountQNA();
            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }

        } else if (category.equals("service")) {
            model.addAttribute("type", "service");
            List<Board_index> service_board = boardService.getServiceBoardPaging(page_index);
            List<Board_index> service_board_notice = boardService.getServiceBoardNotice();

            service_board = boardService.setBoardIndex(service_board);
            service_board_notice = boardService.setBoardIndex(service_board_notice);
            model.addAttribute("user_board", service_board);        // 일반 게시글
            model.addAttribute("notice_board", service_board_notice);    // 공지 게시글


            int size = boardService.getCountService();
            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("page_index", page_index);
        model.addAttribute("sort_type", "new");


        System.out.println("i : " + i);
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);

        page_num = page_index;
        return "community/product/index";
    }

    // qna 게시판 글 목록(페이징 처리 - 좋아요순)
    @GetMapping("/community/{category}/pageByLikes/{page_index}")
    public String view_pageByLikes(@PathVariable("category") String category, @PathVariable("page_index") int page_index, 
    		Model model, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "keyword", required = false) String keyword) throws ParseException {
        int i = 0;
        if (!category.equals("qna") && !category.equals("service")) {
            System.out.println("올바르지 않은 커뮤니티 카테고리");
            return "error";
        }
        List<Integer> page_list = new ArrayList<Integer>();

        if (category.equals("qna")) {
            model.addAttribute("type", "qna");
            List<Board_index> qna_board = boardService.getQnaeBoardPagingByLikes(page_index);
            List<Board_index> qna_board_notice = boardService.getQnaBoardNotice();

            qna_board = boardService.setBoardIndex(qna_board);
            qna_board_notice = boardService.setBoardIndex(qna_board_notice);

            model.addAttribute("user_board", qna_board);        // 일반 게시글
            model.addAttribute("notice_board", qna_board_notice);    // 공지 게시글

            int size = boardService.getCountQNA();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }


        } else if (category.equals("service")) {
            model.addAttribute("type", "service");
            List<Board_index> service_board = boardService.getServiceBoardPagingByLikes(page_index);
            List<Board_index> service_board_notice = boardService.getServiceBoardNotice();

            service_board = boardService.setBoardIndex(service_board);
            service_board_notice = boardService.setBoardIndex(service_board_notice);
            model.addAttribute("user_board", service_board);        // 일반 게시글
            model.addAttribute("notice_board", service_board_notice);    // 공지 게시글


            int size = boardService.getCountService();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("sort_type", "likes");
        model.addAttribute("page_index", page_index);


        System.out.println("i : " + i);
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);

        page_num = page_index;
        return "community/product/index";
    }

    // qna 게시판 글 목록(페이징 처리 - 오래된순)
    @GetMapping("/community/{category}/pageByOld/{page_index}")
    public String view_qna_board_pageByOld(@PathVariable("category") String category, @PathVariable("page_index") int page_index, 
    		Model model, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "keyword", required = false) String keyword) throws ParseException {
        int i = 0;
        if (!category.equals("qna") && !category.equals("service")) {
            System.out.println("올바르지 않은 커뮤니티 카테고리");
            return "error";
        }
        List<Integer> page_list = new ArrayList<Integer>();

        if (category.equals("qna")) {
            model.addAttribute("type", "qna");
            List<Board_index> qna_board = boardService.getQnaBoardPagingByOld(page_index);
            List<Board_index> qna_board_notice = boardService.getQnaBoardNotice();

            qna_board = boardService.setBoardIndex(qna_board);
            qna_board_notice = boardService.setBoardIndex(qna_board_notice);

            model.addAttribute("user_board", qna_board);        // 일반 게시글
            model.addAttribute("notice_board", qna_board_notice);    // 공지 게시글

            int size = boardService.getCountQNA();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }


        } else if (category.equals("service")) {
            model.addAttribute("type", "service");
            List<Board_index> service_board = boardService.getServiceBoardPagingByOld(page_index);
            List<Board_index> service_board_notice = boardService.getServiceBoardNotice();

            service_board = boardService.setBoardIndex(service_board);
            service_board_notice = boardService.setBoardIndex(service_board_notice);
            model.addAttribute("user_board", service_board);        // 일반 게시글
            model.addAttribute("notice_board", service_board_notice);    // 공지 게시글

            int size = boardService.getCountService();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("page_index", page_index);
        model.addAttribute("sort_type", "old");


        System.out.println("i : " + i);
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);

        page_num = page_index;
        return "community/product/index";
    }

    // qna 게시판 글 목록(페이징 처리)
    @GetMapping("/community/{category}/pageByComment/{page_index}")
    public String view_qna_board_pageByComment(@PathVariable("category") String category, @PathVariable("page_index") int page_index, 
    		Model model, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "keyword", required = false) String keyword) throws ParseException {
        int i = 0;
        if (!category.equals("qna") && !category.equals("service")) {
            System.out.println("올바르지 않은 커뮤니티 카테고리");
            return "error";
        }
        List<Integer> page_list = new ArrayList<Integer>();

        if (category.equals("qna")) {
            model.addAttribute("type", "qna");
            List<Board_index> qna_board = boardService.getQnaBoardPagingByComment(page_index);
            List<Board_index> qna_board_notice = boardService.getQnaBoardNotice();

            qna_board = boardService.setBoardIndex(qna_board);
            qna_board_notice = boardService.setBoardIndex(qna_board_notice);

            model.addAttribute("user_board", qna_board);        // 일반 게시글
            model.addAttribute("notice_board", qna_board_notice);    // 공지 게시글

            int size = boardService.getCountQNA();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }


        } else if (category.equals("service")) {
            model.addAttribute("type", "service");
            List<Board_index> service_board = boardService.getServiceBoardPagingByComment(page_index);
            List<Board_index> service_board_notice = boardService.getServiceBoardNotice();

            service_board = boardService.setBoardIndex(service_board);
            service_board_notice = boardService.setBoardIndex(service_board_notice);
            model.addAttribute("user_board", service_board);        // 일반 게시글
            model.addAttribute("notice_board", service_board_notice);    // 공지 게시글

            int size = boardService.getCountService();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("page_index", page_index);
        model.addAttribute("sort_type", "comment");

        System.out.println("i : " + i);
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);

        page_num = page_index;
        return "community/product/index";
    }

    // qna 게시판 글 목록(페이징 처리)
    @GetMapping("/community/{category}/pageByHits/{page_index}")
    public String view_qna_board_pageByHits(@PathVariable("category") String category, @PathVariable("page_index") int page_index, 
    		Model model, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "keyword", required = false) String keyword) throws ParseException {
        int i = 0;
        if (!category.equals("qna") && !category.equals("service")) {
            System.out.println("올바르지 않은 커뮤니티 카테고리");
            return "error";
        }
        List<Integer> page_list = new ArrayList<Integer>();

        if (category.equals("qna")) {
            model.addAttribute("type", "qna");
            List<Board_index> qna_board = boardService.getQnaBoardPagingByHits(page_index);
            List<Board_index> qna_board_notice = boardService.getQnaBoardNotice();

            qna_board = boardService.setBoardIndex(qna_board);
            qna_board_notice = boardService.setBoardIndex(qna_board_notice);

            model.addAttribute("user_board", qna_board);        // 일반 게시글
            model.addAttribute("notice_board", qna_board_notice);    // 공지 게시글

            int size = boardService.getCountQNA();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }


        } else if (category.equals("service")) {
            model.addAttribute("type", "service");
            List<Board_index> service_board = boardService.getServiceBoardPagingByHits(page_index);
            List<Board_index> service_board_notice = boardService.getServiceBoardNotice();

            service_board = boardService.setBoardIndex(service_board);
            service_board_notice = boardService.setBoardIndex(service_board_notice);
            model.addAttribute("user_board", service_board);        // 일반 게시글
            model.addAttribute("notice_board", service_board_notice);    // 공지 게시글

            int size = boardService.getCountService();

            if (size % 20 > 0) {
                i = ((int) (size / 20)) + 1;
            } else {
                i = (int) (size / 20);
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("page_index", page_index);
        model.addAttribute("sort_type", "hits");

        System.out.println("i : " + i);
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);

        page_num = page_index;
        return "community/product/index";
    }


     //qna 게시판 글 전체 갯수 확인 ajax 
    @ResponseBody
    @GetMapping("/community/{category}/count")
    public int get_count_qna(@PathVariable("category") String category) {
        if (category.equals("qna")) {
            return boardService.getCountQNA();
        } else if (category.equals("service")) {
            return boardService.getCountService();
        }
        System.out.println("잘못된 커뮤니티 카테고리");
        return 0;
    }

    // qna 게시판 글 작성(get)
    @GetMapping("/community/{category}/write")
    public String community_product_write(@PathVariable("category") String category, Model model, HttpSession session) {
        if (session.getAttribute("idx") != null) {
            if (category.equals("qna")) {
                model.addAttribute("category", "qna");
                return "community/product/write";
            } else if (category.equals("service")) {
                model.addAttribute("category", "service");
                return "community/product/write";
            } else {
                System.out.println("잘못된 카테고리");
                return null;
            }
        } else {
            return "redirect:/login";
        }
    }

//    //qna 게시판 글 작성(post)
//	@ResponseBody
//	@PostMapping("/community/{category}")
//	public String write_qna(@PathVariable("category") String category, @RequestParam("subject") String subject, @RequestParam("contents") String contents,
//			@RequestParam("attached_file") List<MultipartFile> attached_file,
//			@RequestParam(value = "set_notice", required = false) String set_notice, HttpSession session) throws IOException {
//
//		System.out.println("contents : " + contents);
//		if (session.getAttribute("idx") == null) {
//			System.out.println("로그인 필요");
//			return "0";
//		}
//
//		int member_idx = (int) session.getAttribute("idx");
//
//		if (attached_file.size() > 3) {
//			System.out.println("파일 업로드 갯수 초과");
//			return "4";
//		}
//
//		if (subject == null || subject.length() < 1) {
//			System.out.println("제목 필요");
//			return "subject_failed";
//		}
//
//		if (contents == null || contents.length() < 1) {
//			System.out.println("제목 필요");
//			return "contents_failed";
//		}
//
//		Board board;
//		if (session.getAttribute("session_type").equals("admin") && set_notice != null && set_notice.equals("t")) {
//			// 관리자 회원의 공지글 작성
//			board = new Board('1', subject, contents, 0, 0, memberService.set_now_time(), null, null, null, memberService.findByIdx(member_idx));
//			board.setNotice('t');
//		} else {
//			board = new Board('1', subject, contents, 0, 0, memberService.set_now_time(), null, null, null, memberService.findByIdx(member_idx));
//			board.setNotice('f');
//		}
//
//		if (category.equals("qna")) {
//			board = boardService.save(board);
//			if (board == null) {
//				System.out.println("db insert 오류");
//				return "4";
//			}
//
//			List<Board_file> bfs = new ArrayList<Board_file>();
//
//			if (attached_file != null) {    // 파일 첨부가 되었으면
//				//aws s3 새 파일 업로드 및 board, board_file DB insert
//				for (int i = 0; i < attached_file.size(); i++) {
//					if (i >= 3) {
//						break;
//					}
//					if (attached_file.get(i).isEmpty()) {
//						continue;
//					}
//					if (attached_file.get(i).getSize() == 0) {
//						continue;
//					}
//					String original_file_name = attached_file.get(i).getOriginalFilename();
//					if (memberService.checkFileName(original_file_name) == false) {
//						System.out.println("잘못된 파일명");
//						return "5";
//					}
//					String file_name = memberService.getUniqFileName(original_file_name);    // S3에 저장할 unique 파일 이름
//					InputStream input = null;
//					try {
//						input = attached_file.get(i).getInputStream();
//						amazonS3Service.putThumbnail("community/qna", file_name, input, amazonS3Service.getMetadata(attached_file.get(i)));
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						// AWS S3 업로드 오류
//						return "2";
//					} finally {
//						if (input != null) {
//							input.close();
//							input = null;
//						}
//					}
//					String attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/qna/" + file_name;
//					Board_file bf = new Board_file(board.getIdx(), original_file_name, file_name, attached_file_url, (int) attached_file.get(i).getSize());
//					bf = boardService.save(bf);
//					bfs.add(bf);
//					if (bf == null) {
//						System.out.println("db insert 오류");
//						amazonS3Service.deleteThumbnail("community/qna", file_name);
//						return "4";
//					}
//				}
//			}
//			return "3";        // success
//
//		} else if (category.equals("service")) {
//			board.setCategory('2');
//			board = boardService.save(board);
//			if (board == null) {
//				System.out.println("db insert 오류");
//				return "4";
//			}
//
//			List<Board_file> bfs = new ArrayList<Board_file>();
//
//			if (attached_file != null) {    // 파일 첨부가 되었으면
//				//aws s3 새 파일 업로드 및 board, board_file DB insert
//				for (int i = 0; i < attached_file.size(); i++) {
//					if (i >= 3) {
//						break;
//					}
//					if (attached_file.get(i).isEmpty()) {
//						continue;
//					}
//					if (attached_file.get(i).getSize() == 0) {
//						continue;
//					}
//					String original_file_name = attached_file.get(i).getOriginalFilename();
//					String file_name = memberService.getUniqFileName(original_file_name);    // S3에 저장할 unique 파일 이름
//					InputStream input = null;
//					try {
//						input = attached_file.get(i).getInputStream();
//						amazonS3Service.putThumbnail("community/service", file_name, input, amazonS3Service.getMetadata(attached_file.get(i)));
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						// AWS S3 업로드 오류
//						return "2";
//					} finally {
//						if (input != null) {
//							input.close();
//							input = null;
//						}
//					}
//					String attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/service/" + file_name;
//					Board_file bf = new Board_file(board.getIdx(), original_file_name, file_name, attached_file_url, (int) attached_file.get(i).getSize());
//					bf = boardService.save(bf);
//					bfs.add(bf);
//					if (bf == null) {
//						System.out.println("db insert 오류");
//						amazonS3Service.deleteThumbnail("community/service", file_name);
//						return "4";
//					}
//				}
//			}
//			return "3";        // success
//		}
//		System.out.println("잘못된 카테고리");
//		return null;
//
//	}

    // qna 게시판 글 작성(post)
    @ResponseBody
    @PostMapping("/community/{category}")
    public String write_qna(@PathVariable("category") String category, @RequestParam("subject") String subject, 
    		@RequestParam("contents") String contents, @RequestParam(value = "set_notice", required = false) String set_notice, HttpSession session) throws IOException {

        System.out.println("contents : " + contents);
        if (session.getAttribute("idx") == null) {
            System.out.println("로그인 필요");
            return "0";
        }

        int member_idx = (int) session.getAttribute("idx");
        String session_tmp_name, tmp_bucket_name, des_bucket_name;
        List<TmpFileInfo> session_tmp_file;
        Board board;

        // 게시글 정보 셋팅 및 db insert
        if (category.equals("qna")) {
            session_tmp_name = "product_tmp_file";
            session_tmp_file = (List<TmpFileInfo>) session.getAttribute("product_tmp_file");
            board = new Board('1', subject, contents, 0, 0, memberService.set_now_time(), null, null, null, memberService.findByIdx(member_idx));

            tmp_bucket_name = "community/qna_tmp";
            des_bucket_name = "community/qna/" + memberService.set_now_date();

        } else if (category.equals("service")) {
            session_tmp_name = "service_tmp_file";
            session_tmp_file = (List<TmpFileInfo>) session.getAttribute("service_tmp_file");
            board = new Board('2', subject, contents, 0, 0, memberService.set_now_time(), null, null, null, memberService.findByIdx(member_idx));

            tmp_bucket_name = "community/service_tmp";
            des_bucket_name = "community/service/" + memberService.set_now_date();
        } else {
            System.out.println("잘못된 카테고리");
            return null;
        }

        if (session.getAttribute("session_type").equals("admin") && set_notice != null && set_notice.equals("t")) {
            board.setNotice('t');
        } else {
            board.setNotice('f');
        }

        if (session_tmp_file != null && session_tmp_file.size() > 3) {
            System.out.println("파일 업로드 갯수 초과");
            return "4";
        }

        if (subject == null || subject.length() < 1) {
            System.out.println("제목 필요");
            return "subject_failed";
        }

        if (contents == null || contents.length() < 1) {
            System.out.println("제목 필요");
            return "contents_failed";
        }

        board = boardService.save(board);

        if (board == null) {
            System.out.println("db insert 오류");
            return "4";
        }


        // 파일 정보 db insert, s3 업로드, 기존 temp s3 삭제 및 세션에 존재하는 temp file 정보 삭제
        String contents2 = board.getContents();
        List<Board_file> bfs = new ArrayList<Board_file>();
        if (session_tmp_file != null && session_tmp_file.size() > 0) {    // 파일 첨부가 되었으면
            //aws s3 새 파일 업로드 및 board, board_file DB insert
            for (int i = 0; i < session_tmp_file.size(); i++) {
                if (i >= 3) {
                    break;
                }
                String server_file_name = session_tmp_file.get(i).getServer_file_name();
                String original_file_name = session_tmp_file.get(i).getOriginal_file_name();
                if (memberService.checkFileName(original_file_name) == false) {
                    System.out.println("잘못된 파일명");
                    return "5";
                }

                // s3 upload 버킷에  tmp 버킷 파일 가져와 복사하고, tmp 버킷 파일은 삭제하기
                amazonS3Service.copyObject(tmp_bucket_name, server_file_name, des_bucket_name, server_file_name);
                amazonS3Service.deleteThumbnail(tmp_bucket_name, server_file_name);	

                String attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/" + des_bucket_name + "/" + server_file_name;
                Board_file bf = new Board_file(board.getIdx(), original_file_name, server_file_name, attached_file_url, (int) session_tmp_file.get(i).getFile_size());
                bf = boardService.save(bf);
                bfs.add(bf);
                if (bf == null) {
                    System.out.println("db insert 오류");
                    amazonS3Service.deleteThumbnail(des_bucket_name, session_tmp_file.get(i).getOriginal_file_name());
                    return "4";
                }
                
                // board update contents (file url)
                String tmp_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/" + tmp_bucket_name + "/" + server_file_name;
                contents2 = contents2.replace(tmp_file_url, attached_file_url);
                boardService.updateContents(board.getIdx(), contents2);
            }

            session.removeAttribute(session_tmp_name);
        }
        return "3";        // success

    }


    // ckEditor 파일 업로드. 파일업로드 최대 3개 가능. 클라이언트에서 이미지첨부시 임시폴더에 일단 저장해놓는 코드. 저장후 s3 이미지경로 리턴
    @ResponseBody
    @PostMapping("/community/summernote/upload/{category}")
    public List<String> ckEditor_upload_file(@PathVariable("category") String category, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam("upload") List<MultipartFile> upload, HttpSession session) {
        System.out.println("ckeditor 들어옴");
        List<String> total_url;
        List<TmpFileInfo> tmp_file;

        if (upload != null && upload.size() > 0) {
            total_url = new ArrayList<String>();
            tmp_file = new ArrayList<TmpFileInfo>();
            String bucket_name, session_tmp_name, tmp_bucket_name;

            if (category.equals("qna")) {
                bucket_name = "community/qna_tmp";
                session_tmp_name = "product_tmp_file";
                tmp_bucket_name = "qna_tmp";
            } else if (category.equals("service")) {
                bucket_name = "community/service_tmp";
                session_tmp_name = "service_tmp_file";
                tmp_bucket_name = "service_tmp";
            } else {
                System.out.println("잘못된 버킷이름");
                return null;
            }

            for (int i = 0; i < upload.size(); i++) {
                TmpFileInfo info = new TmpFileInfo();

                String attached_file_url = null;
                String original_file_name = upload.get(i).getOriginalFilename();
                String file_name = memberService.getUniqFileName(original_file_name);    // S3에 저장할 unique 파일 이름

                info.setOriginal_file_name(original_file_name);
                info.setServer_file_name(file_name);
                info.setFile_size(upload.get(i).getSize());

                try {
                    // S3에 파일 업로드
                    System.out.println("temp upload : " + original_file_name);
                    amazonS3Service.putThumbnail(bucket_name, file_name, upload.get(i).getInputStream(), amazonS3Service.getMetadata(upload.get(i)));
               
                    // ckEditor응답에 업로드된 파일 URL 전송
                    attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/" + tmp_bucket_name + "/" + file_name;

                    tmp_file.add(info);
                    total_url.add(attached_file_url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            if (tmp_file != null && tmp_file.size() > 0) {
                if (session.getAttribute(session_tmp_name) == null) {
                    session.setAttribute(session_tmp_name, tmp_file);
                } else {
                    List<TmpFileInfo> session_tmp_file = (List<TmpFileInfo>) session.getAttribute(session_tmp_name);
                    System.out.println("session tmp size : " + session_tmp_file.size());
                    session_tmp_file.addAll(tmp_file);
                    session.setAttribute(session_tmp_name, session_tmp_file);
                }
            }

            return total_url;
        } else {
            return null;
        }

    }

    @GetMapping("/community/{category}/write_cancel")
    public String write_cancel(@PathVariable("category") String category, HttpSession session) {
        if (category.equals("qna")) {
            if (session.getAttribute("product_tmp_file") != null) {
                session.removeAttribute("product_tmp_file");
            }
        } else if (category.equals("service")) {
            if (session.getAttribute("service_tmp_file") != null) {
                session.removeAttribute("service_tmp_file");
            }
        } else {
            System.out.println("카테고리에러");
        }
        return "";
    }

    // QnA 게시판에서 특정글 수정
    @GetMapping("/community/{category}/modify/{board_idx}")
    public String modify_qna_get(@PathVariable("category") String category, @PathVariable("board_idx") int board_idx, Model model) {
        Board board = boardService.findOne(board_idx);
        //List<Board_file> bf = boardService.findBf(board_idx);
        List<Board_file> bf = board.getBoard_files();
        model.addAttribute("board", board);
        model.addAttribute("category", category);
        if (bf != null && bf.size() > 0) {
            model.addAttribute("board_file", bf);
        }
        if (category.equals("qna")) {
            return "community/product/modify";
        } else if (category.equals("service")) {
            return "community/product/modify";
        } else {
            System.out.println("잘못된 카테고리");
            return null;
        }

    }

    // QnA 게시판에서 특정글 수정
    @ResponseBody
    @PostMapping("/community/{category}/modify/{board_idx}")
    public String modify_qna(@PathVariable("category") String category, @PathVariable("board_idx") int board_idx,
                             @RequestParam("subject") String subject, @RequestParam("contents") String contents,
                             @RequestParam(value="attached_file", required=false) MultipartFile[] attached_file,
                             @RequestParam(value="image_legacy", required=false) String[] deleted_image, 
                             HttpSession session) throws IOException {
        
        if (subject == null || subject.length() < 1) {
            System.out.println("제목 필요");
            return "subject_failed";
        }

        if (contents == null || contents.length() < 1) {
            System.out.println("제목 필요");
            return "contents_failed";
        }

        Board board = boardService.findOne(board_idx);
        if (board.getMember().getIdx() != (int) session.getAttribute("idx")) {
            System.out.println("수정 권한이 없습니다.");
            return "1";
        }
        
        String board_write_date = board.getDate_created().substring(0, 10);
        
        List<TmpFileInfo> session_tmp_file;	// 이 객체에 값이 있으면 새로 업로드된 이미지가 있는것임.
        String session_tmp_name, tmp_bucket_name, des_bucket_name;
        
        
        // 게시글 정보 셋팅 및 db insert
        if (category.equals("qna")) {
            session_tmp_name = "product_tmp_file";
            session_tmp_file = (List<TmpFileInfo>) session.getAttribute("product_tmp_file");
            
            tmp_bucket_name = "community/qna_tmp";
            des_bucket_name = "community/qna/" + board_write_date;

        } else if (category.equals("service")) {
            session_tmp_name = "service_tmp_file";
            session_tmp_file = (List<TmpFileInfo>) session.getAttribute("service_tmp_file");
           
            tmp_bucket_name = "community/service_tmp";
            des_bucket_name = "community/service/" + board_write_date;
        } else {
            System.out.println("잘못된 카테고리");
            return null;
        }
        
        
        // tmp 버킷에 올라간 이미지 복사 (des_bucket_name으로)
        String attached_file_url = null;

        if (category.equals("qna")) {
            attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/qna/" + board_write_date;
        } else if (category.equals("service")) {
            attached_file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/service/" + board_write_date;
        } else {
            System.out.println("잘못된 카테고리");
            return null;
        }

      
        // 파일 삭제 여부 확인
        List<Board_file> bfs = boardService.findBf(board_idx);
        
        if(bfs != null && bfs.size() > 0) {
        	for(int i=0 ; i < bfs.size() ; i++) {
        		String file_url = bfs.get(i).getFile_url();
        		if(contents.contains(file_url) == false) {	// 기존 첨부되었던 s3_url String이 변경된 contents에 존재하지 않으면
        			int bf_idx = bfs.get(i).getIdx();
        			boardService.deleteBF(bf_idx);	// 삭제됐음으로 판단하고 해당 board_file db, s3 객체 삭제
        			amazonS3Service.deleteThumbnail(des_bucket_name, bfs.get(i).getServer_filename());
        		}
        	}
        }
        
        
        // 새파일이 업로드 되었을시
        // contents s3 url update 필수
        String contents2 = null;
        if (session_tmp_file != null && session_tmp_file.size() > 0) {
        	
        	if (session_tmp_file.size() > 3) {
                System.out.println("파일 업로드 갯수 초과");
                return "4";
            }
        	
            //aws s3 새 파일 업로드 및 board_file DB insert
            for (int i = 0; i < session_tmp_file.size(); i++) {
               
                String original_file_name = session_tmp_file.get(i).getOriginal_file_name();
                String file_name = session_tmp_file.get(i).getServer_file_name();

                amazonS3Service.copyObject(tmp_bucket_name, file_name, des_bucket_name, file_name);
				amazonS3Service.deleteThumbnail(tmp_bucket_name, file_name); 

                Board_file bf = new Board_file(board_idx, original_file_name, file_name, (attached_file_url + "/" + file_name), (int) session_tmp_file.get(i).getFile_size());
                boardService.save(bf);
            
                contents2 = contents.replace(("https://s3.ap-northeast-2.amazonaws.com/littleone/" + tmp_bucket_name + "/" + file_name),
                		bf.getFile_url());
            }

            
            session.removeAttribute(session_tmp_name); 		// 세션에 있는 임시파일 정보를 지운다.
        } else {
        	// 새파일이 첨부안되었을때
        	contents2 = contents;
        }
        
        // board DB update
        int update_result = boardService.update_file(board_idx, subject, contents2, memberService.set_now_time());
        if (update_result != 1) {
            System.out.println("DB update 오류");
            return "3";
        }
        
        
        return "success";
    }
    

    // QNA 게시판에서 특정글 삭제
    @ResponseBody
    @GetMapping("/community/{category}/delete/{board_idx}")
    public String delete_qna(@PathVariable("category") String category, @PathVariable("board_idx") int board_idx, HttpSession session) {
        if (session.getAttribute("idx") == null) {
            System.out.println("세션이 필요함");
            return "0";
        }
        Board board = boardService.findOne(board_idx);
        if (board == null) {
            System.out.println("존재하지 않는 게시글입니다.");
            return "1";
        }
        if (board.getMember().getIdx() != (int) session.getAttribute("idx")) {
            System.out.println("삭제 권한이 없습니다.");
            return "2";
        }

        boardService.delete(board_idx);
        return "success";
    }

    // QnA 게시판에서 특정글을 클릭
    @GetMapping("/community/{category}/{board_idx}")
    public String read_qna(@PathVariable("category") String category, @PathVariable("board_idx") int board_idx, Model model, HttpSession session) {
        if (session.getAttribute("idx") == null) {
            System.out.println("세션이 필요함");
            return "redirect:/login";
        }

        int member_idx = (int) session.getAttribute("idx");
        Member session_user = memberService.findByIdx(member_idx);    // 로그인된 회원정보
        Board board = boardService.findOne(board_idx);                  // 게시글정보
        if (board == null) {
            System.out.println("존재하지 않는 게시물");
            return "error";
        }
        if (category.equals("qna") == false && category.equals("service") == false) {
            System.out.println("올바르지 않은 카테고리");
            return "error";
        }

        List<Board_file> bf = boardService.findBf(board_idx);    // 첨부파일
        Member writer = board.getMember();    // 작성자정보
        Board_likes bl = boardService.find_likes(member_idx, board_idx);    // 게시글좋아요 정보
        List<Comment> comment_list = boardService.findComment(board_idx);
        //List<Comment_check> comment_list = boardService.getListCommentCheck(board_idx);
        Board prevBoard, nextBoard;
        if ((int) session.getAttribute("idx") != board.getMember().getIdx()) {
            boardService.updateHits(board_idx);        // 조회수 +1 update (세션 유저가 글쓴이가 아니면)
        }

        if (comment_list != null && comment_list.size() > 0) {
            for (int i = 0; i < comment_list.size(); i++) {
                Comment_likes cl = boardService.find(member_idx, comment_list.get(i).getIdx());
                if (cl != null) {
                    comment_list.get(i).setLikes_check(1);
                }
                try {
                    comment_list.get(i).setDate_created(galleryService.setViewDate(comment_list.get(i).getDate_created()));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //comment_list = setLikesCheck(comment_list, member_idx);
            model.addAttribute("comment_list", comment_list);
        }
        if (bl != null) {
            model.addAttribute("likes_check", "true");
        }
        if (bf.size() > 0) {
            model.addAttribute("board_file", bf);
        }

        String contents = board.getContents();
        
        model.addAttribute("board", board);
        
        if(contents != null && contents.trim().length() > 0) {
        	model.addAttribute("parsing_contents", boardService.parsingHtml(contents));
        }
        
        model.addAttribute("writer", writer);
        model.addAttribute("my_nickname", session_user.getNickname());
        model.addAttribute("page_num", page_num);
        model.addAttribute("category", category);

        if (category.equals("qna")) {
            prevBoard = boardService.getPreviousBoard(board_idx, '1');
            nextBoard = boardService.getNextBoard(board_idx, '1');
            if (prevBoard != null) {
                model.addAttribute("prevBoard", prevBoard);
            }
            if (nextBoard != null) {
                model.addAttribute("nextBoard", nextBoard);
            }

            return "community/product/view";
        } else if (category.equals("service")) {
            prevBoard = boardService.getPreviousBoard(board_idx, '2');
            nextBoard = boardService.getNextBoard(board_idx, '2');
            if (prevBoard != null) {
                model.addAttribute("prevBoard", prevBoard);
            }
            if (nextBoard != null) {
                model.addAttribute("nextBoard", nextBoard);
            }
            return "community/product/view";
        } else {
            System.out.println("잘못된 카테고리");
            return null;
        }

    }

    // 댓글 알림을 통해 게시글에 접근할때
    @GetMapping("/community/{board_idx}/{comment_idx}/alert/{alarm_idx}")
    public String read_qna_alert(@PathVariable("board_idx") int board_idx, @PathVariable("comment_idx") int comment_idx,
                                 @PathVariable("alarm_idx") int alarm_idx, Model model, HttpSession session) {
        // 알림 db 삭제
        if (alarmService.findAlert(alarm_idx) != null) {
            alarmService.updateReadChk(alarm_idx);
        }

        int member_idx = (int) session.getAttribute("idx");
        //Member session_user = memberService.findByIdx(member_idx);    // 로그인된 회원정보
        Board board = boardService.findOne(board_idx);            // 게시글정보
        if (board == null) {
            System.out.println("존재하지 않는 게시물");
            return "error";
        }
        List<Board_file> bf = board.getBoard_files();    // 첨부파일
        Member writer = board.getMember();    // 작성자정보
        Board_likes bl = boardService.find_likes(member_idx, board_idx);    // 게시글좋아요 정보
        List<Comment> comment_list = boardService.findComment(board_idx);
        Board prevBoard, nextBoard;

        if (comment_list != null && comment_list.size() > 0) {
            for (int i = 0; i < comment_list.size(); i++) {
                Comment_likes cl = boardService.find(member_idx, comment_list.get(i).getIdx());
                if (cl != null) {
                    comment_list.get(i).setLikes_check(1);
                }
                try {
                    comment_list.get(i).setDate_created(galleryService.setViewDate(comment_list.get(i).getDate_created()));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            model.addAttribute("comment_list", comment_list);
        }
        if (bl != null) {
            model.addAttribute("likes_check", "true");
        }
        if (bf.size() > 0) {
            model.addAttribute("board_file", bf);
        }

        model.addAttribute("board", board);
        model.addAttribute("writer", writer);
        model.addAttribute("my_nickname", writer.getNickname());
        model.addAttribute("alert_comment_idx", comment_idx);

        if (board.getCategory() == '1') {
            prevBoard = boardService.getPreviousBoard(board_idx, '1');
            nextBoard = boardService.getNextBoard(board_idx, '1');
            if (prevBoard != null) {
                model.addAttribute("prevBoard", prevBoard);
            }
            if (nextBoard != null) {
                model.addAttribute("nextBoard", nextBoard);
            }

            return "community/product/view";
        } else if (board.getCategory() == '2') {
            prevBoard = boardService.getPreviousBoard(board_idx, '2');
            nextBoard = boardService.getNextBoard(board_idx, '2');
            if (prevBoard != null) {
                model.addAttribute("prevBoard", prevBoard);
            }
            if (nextBoard != null) {
                model.addAttribute("nextBoard", nextBoard);
            }
            return "community/product/view";
        } else {
            System.out.println("잘못된 카테고리");
            return "error";
        }
    }

    // 특정 글의 댓글 리스트 가져옴
    @ResponseBody
    @GetMapping("/community/qna/comment/{board_idx}")
    public List<Comment> get_comment(@PathVariable("board_idx") int board_idx, Model model, HttpSession session) {
        List<Comment> comment_list = boardService.findComment(board_idx);
        if (comment_list != null && comment_list.size() > 0) {
            return comment_list;
        } else {
            return null;
        }
    }

    // 댓글 작성 ajax
    @ResponseBody
    @PostMapping("/community/comment/{board_idx}")
    public Comment_data post_comment(@PathVariable("board_idx") int board_idx, @RequestParam("reply_contents") String reply_contents,
                                     HttpSession session) {
        Comment_data cd = new Comment_data();
        if (session.getAttribute("idx") == null) {
            System.out.println("세션이 필요함");
            cd.setResult(1);
            cd.setComments(null);
        }
        int member_idx = (int) session.getAttribute("idx");
        Member member = memberService.findByIdx(member_idx);
        Comment comment = new Comment(board_idx, member, '1', reply_contents, memberService.set_now_time(), null);
        comment = boardService.save(comment);
        if (comment == null) {
            System.out.println("DB insert 오류");
            cd.setResult(2);
            cd.setComments(null);
        }

        // 댓글 작성 알림 db insert
        int writer_idx = boardService.findOne(board_idx).getMember().getIdx();
        if (member_idx != writer_idx) {
            Alert alert = new Alert();
            alert.setEvent_type('2');    // 신규 댓글 작성된 알림
            alert.setRequester(member_idx);
            alert.setRecipient(writer_idx);
            alert.setRequest_date(memberService.set_now_time());
            alert = alarmService.insert(alert);

            if (alert != null) {
                Alert_board ab = new Alert_board();
                ab.setIdx(alert.getIdx());
                ab.setBoard_idx(board_idx);
                ab.setComment_idx(comment.getIdx());
                alarmService.saveAlert_board(ab);
            } else {
                System.out.println("alert_board insert error");
            }
        }

        //List<Comment> comment_list = boardService.findComment(board_idx);
        List<Comment_check> list = boardService.getListCommentCheck(board_idx);
        if (list != null && list.size() > 0) {
            // 정상적으로 댓글리스트를 가져옴
            cd.setResult(3);
            cd.setComments(setLikesCheck(list, member_idx));
        } else {
            // 댓글이 존재하지 않음
            cd.setResult(4);
            cd.setComments(null);
        }
        return cd;
    }

    // 댓글 삭제 ajax
    @ResponseBody
    @GetMapping("/community/commentDelete/{comment_idx}/{board_idx}")
    public Comment_data delete_comment(@PathVariable("comment_idx") int comment_idx, @PathVariable("board_idx") int board_idx, HttpSession session) {
        Comment comment = boardService.findCommentOne(comment_idx);
        Comment_data data = new Comment_data();
        data.setComments(null);
        if (session.getAttribute("idx") == null) {
            data.setResult(1);
            return data;
        }
        if (comment.getMember().getIdx() != (int) session.getAttribute("idx")) {
            data.setResult(2);
            return data;
        }
        int member_idx = (int) session.getAttribute("idx");

        Alert_board alert_board = alarmService.findByComment_idx(comment_idx);
        if (alert_board != null) {
            alarmService.deleteAlert(alert_board.getIdx());
        }

        boardService.deleteCommentLikes(member_idx, comment_idx);
        boardService.deleteComment(comment_idx);

        List<Comment_check> list = boardService.getListCommentCheck(board_idx);
        data.setComments(setLikesCheck(list, member_idx));
        data.setResult(3);
        return data;
    }

    // 댓글 수정 ajax
    @ResponseBody
    @PostMapping("/community/commentModify/{comment_idx}/{board_idx}")
    public Comment_data modify_comment(@RequestParam("contents") String contents, @PathVariable("comment_idx") int comment_idx,
                                       @PathVariable("board_idx") int board_idx, HttpSession session) {
        Comment comment = boardService.findCommentOne(comment_idx);
        Comment_data data = new Comment_data();
        data.setComments(null);
        if (session.getAttribute("idx") == null) {
            data.setResult(1);
            return data;
        }
        if (comment.getMember().getIdx() != (int) session.getAttribute("idx")) {
            data.setResult(2);
            return data;
        }
        int member_idx = (int) session.getAttribute("idx");
        boardService.updateComment(comment_idx, contents, memberService.set_now_time());
        List<Comment_check> list = boardService.getListCommentCheck(board_idx);
        data.setComments(setLikesCheck(list, member_idx));
        data.setResult(3);
        return data;
    }

    // 게시글 좋아요버튼 클릭시 ajax
    @ResponseBody
    @GetMapping("/community/{board_idx}/likes")
    public String like_qna(@PathVariable("board_idx") int board_idx, HttpSession session) {
        StringBuffer result = new StringBuffer();
        if (session.getAttribute("idx") == null) {
            return "error";
        }
        int member_idx = (int) session.getAttribute("idx");
        Board_likes likes = boardService.find_likes(member_idx, board_idx);
        if (likes == null) {
            // 좋아요를 안누른 게시글인 경우
            result.append("plus:");
            likes = new Board_likes(member_idx, board_idx);
            boardService.save_likes(likes);
            // 좋아요 +1 업데이트
            boardService.updateLikes(board_idx);
        } else {
            // 이미 좋아요를 누른 게시글인 경우
            result.append("minus:");
            boardService.deleteLikes(member_idx, board_idx);
            // 좋아요 -1 업데이트
            boardService.updateLikesM(board_idx);
        }
        Board board = boardService.findOne(board_idx);
        if (board == null) {
            return "error2";
        }
        result.append(board.getLikes());
        return result.toString();
    }

    // 댓글 좋아요 클릭시
    @ResponseBody
    @GetMapping("/community/commentLikes/{comment_idx}")
    public String like_comment_qna(@PathVariable("comment_idx") int comment_idx, HttpSession session) {
        if (session.getAttribute("idx") == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        int member_idx = (int) session.getAttribute("idx");
        Comment_likes likes = boardService.find(member_idx, comment_idx);
        if (likes == null) {
            result.append("plus:");
            likes = new Comment_likes(member_idx, comment_idx);
            boardService.saveCommentLikes(likes);
            boardService.updateCommentLikes(comment_idx);
        } else {
            result.append("minus:");
            boardService.deleteCommentLikes(member_idx, comment_idx);
            boardService.updateCommentLikesM(comment_idx);
        }
        Comment comment = boardService.findCommentOne(comment_idx);
        result.append(comment.getLikes());
        return result.toString();
    }

    // 나의 커뮤니티 작성글 리스트
    @GetMapping("/community/member_writing/{member_idx}/{page_index}")
    public String my_writing_list(@PathVariable("member_idx") int member_idx, @PathVariable("page_index") int page_index,
                                  HttpSession session, Model model) {
        if (session.getAttribute("idx") == null) {
            System.out.println("세션이 존재하지 않음");
            return "login";
        }
        List<Board_index> my_list = boardService.getListByMember_idx(member_idx, page_index);
        if(my_list != null && my_list.size() > 0) {
        	my_list = boardService.setBoardIndex(my_list);
        }
        model.addAttribute("member", memberService.findByIdx(member_idx));
        model.addAttribute("my_list", my_list);
        return "user/view";
    }

    // QNA 커뮤니티에서 검색
    //community/qna/search?keyword={search_word}&type={search_type}
    @GetMapping("/community/{category}/search/{page_index}")
    public String searchBySubjectQNA(@PathVariable("category") String category, @PathVariable("page_index") int page_index,
                                     @RequestParam("keyword") String search_word, @RequestParam("type") String search_type, Model model) {
        if (search_word == null || search_word.length() == 0 && search_type == null || search_type.length() == 0) {
            System.out.println("입력값이 필요");
            return "redirect:/community/qna/page/1";
        }
        List<Board_index> board_list;
        char category2 = '0';

        if (category.equals("service") == false && category.equals("qna") == false) {
            System.out.println("잘못된 카테고리");
            return "error";
        }

        if (category.equals("qna")) {
            category2 = '1';
        } else if (category.equals("service")) {
            category2 = '2';
        }

        if (search_type.equals("writer")) {
            board_list = boardService.searchByNickname(search_word, category2, page_index);
            model.addAttribute("type", "writer");
        } else if (search_type.equals("title")) {
            board_list = boardService.searchBySubject(search_word, category2, page_index);
            model.addAttribute("type", "title");
        } else if (search_type.equals("contents")) {
            board_list = boardService.searchByContents(search_word, category2, page_index);
            model.addAttribute("type", "contents");
        } else {
            System.out.println("잘못된 접근입니다.");
            return "error";
        }

        search_result_count = board_list.size();

        board_list = boardService.setBoardIndex(board_list);

        model.addAttribute("user_board", board_list);

        int size = board_list.size();
        int i = 0;
        List<Integer> page_list = new ArrayList<Integer>();

        if (size % 20 > 0) {
            i = ((int) (size / 20)) + 1;
        } else {
            i = (int) (size / 20);
        }
        for (int index = 1; index <= i; index++) {
            page_list.add(index);
        }
        model.addAttribute("page_size", page_list);
        model.addAttribute("page_index", page_index);

        return "community/product/index";

    }

    // 게시글  결과 갯수 ajax
    @ResponseBody
    @GetMapping("/community/qna/search_result_count")
    public int searchResultCount() {
        return search_result_count;
    }

    public List<Comment_check> setLikesCheck(List<Comment_check> list, int member_idx) {
        for (int i = 0; i < list.size(); i++) {
            Comment_likes likes = boardService.find(member_idx, list.get(i).getIdx());
            if (likes != null) {
                list.get(i).setLikes_check(1);
            }
            try {
                list.get(i).setDate_created(galleryService.setViewDate(list.get(i).getDate_created()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return list;
    }

    // 커뮤니티 작성자 썸네일 클릭시 회원정보
	@GetMapping("/community/view_member_info/{member_idx}/board/{page_index}")
	public String viewMemberInfoPaging(@PathVariable("member_idx") int member_idx, @PathVariable("page_index") int page_index,
			Model model, HttpSession session) throws ParseException {
		Member member = memberService.findByIdx(member_idx);
		List<Board_index> board = boardService.getListByMember_idx(member_idx, page_index);
		int comment_count = boardService.countComment(member_idx);
		if(member != null) {
			model.addAttribute("member", member);
		} else {
			return "error";
		}

		if(board != null) {
			model.addAttribute("board", board);
		}
		model.addAttribute("comment_count", comment_count);
		model.addAttribute("totalLikes", boardService.getTotalLikes(member_idx));
		
		Member_mng mng = mngService.findOne(member_idx);
		
		model.addAttribute("point", mng.getPoint());
		model.addAttribute("grade", mng.getGrade());
		
		Integer infant_idx = (Integer) session.getAttribute("infant_idx");
		if(infant_idx != null) {
			Infant infant = infantService.findOne(infant_idx);
			model.addAttribute("infant", infant);
			model.addAttribute("infant_age", infantService.getInfantYear(infant.getBirth()));
		}
		
		
		
		
		return "user/view";
	}

    // 회원 작성 게시글 전체 갯수 ajax
    @ResponseBody
    @GetMapping("/community/view_member_info/{member_idx}/gallery_count")
    public int viewMemberInfoBoardCount(@PathVariable("member_idx") int member_idx) {
        return boardService.getCountMemberBoard(member_idx);
    }
}
