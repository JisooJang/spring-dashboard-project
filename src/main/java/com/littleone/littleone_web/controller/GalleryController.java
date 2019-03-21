package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.littleone.littleone_web.domain.Board_index;
import com.littleone.littleone_web.domain.Comment;
import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Comment_data;
import com.littleone.littleone_web.domain.GalleryLikesInfo;
import com.littleone.littleone_web.domain.Gallery_index;
import com.littleone.littleone_web.domain.Id_comment;
import com.littleone.littleone_web.domain.Id_comment_likes;
import com.littleone.littleone_web.domain.Id_file;
import com.littleone.littleone_web.domain.Id_likes;
import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Infant_diary;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.BoardService;
import com.littleone.littleone_web.service.DashBoardService;
import com.littleone.littleone_web.service.GalleryService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;

@Controller
public class GalleryController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private DashBoardService dashboardService;

	@Autowired
	private GalleryService galleryService;

	@Autowired
	private AmazonS3Service amazonS3Service;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService mngService;

	@Autowired
	private AlertService alarmService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
	private InfantService infantService;

	
	
	// 기본 갤러리 리스트 (최신순)
	@GetMapping("/gallery")
	public String gallery_view(Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListIndexPaging(1);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			model.addAttribute("gallery_list", gallery_list);
		}
		model.addAttribute("sort_type", "new");
		return "gallery/index";
	}

	// 갤러리 목록(최신순) 페이징 처리 (ajax)
	@ResponseBody
	@GetMapping("/gallery/paging/{page_index}")
	public List<Gallery_index> gallery_view_paging(@PathVariable("page_index") int page_index, Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListIndexPaging(page_index);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			return gallery_list;
		} else {
			return null;
		}
	}

	// 오래된순 갤러리 리스트
	@GetMapping("/gallery/pageByOld")
	public String gallery_view_old(Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByOld(1);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			model.addAttribute("gallery_list", gallery_list);
		}
		model.addAttribute("sort_type", "old");
		return "gallery/index";
	}

	// 오래된순 갤러리 리스트 페이징 ajax
	@ResponseBody
	@GetMapping("/gallery/pageByOld/{page_index}")
	public List<Gallery_index> gallery_view_old(@PathVariable("page_index") int page_index, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByOld(page_index);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
		}
		return gallery_list;
	}

	// 좋아요순 갤러리 리스트
	@GetMapping("/gallery/pageByLikes")
	public String gallery_view_likes(Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByLikes(1);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			model.addAttribute("gallery_list", gallery_list);
		}
		model.addAttribute("sort_type", "likes");
		return "gallery/index";
	}

	// 갤러리 목록(좋아요순) 페이징 ajax
	@ResponseBody
	@GetMapping("/gallery/paging_likes/{page_index}")
	public List<Gallery_index> gallery_view_paging_likes(@PathVariable("page_index") int page_index, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByLikes(page_index);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			return gallery_list;
		} else {
			return null;
		}
	}

	// 댓글순 갤러리 리스트
	@GetMapping("/gallery/pageByComments")
	public String gallery_view_comments(Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByComments(1);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			model.addAttribute("gallery_list", gallery_list);
		}
		model.addAttribute("sort_type", "comments");
		return "gallery/index";
	}

	// 댓글순 갤러리 리스트 페이징 ajax
	@ResponseBody
	@GetMapping("/gallery/pageByComments/{page_index}")
	public List<Gallery_index> gallery_view_comments(@PathVariable("page_index") int page_index, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByComments(page_index);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
		}
		return gallery_list;
	}

	// 조회수순 갤러리 리스트
	@GetMapping("/gallery/pageByHits")
	public String gallery_view_hits(Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByHits(1);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
			model.addAttribute("gallery_list", gallery_list);
		}
		model.addAttribute("sort_type", "hits");
		return "gallery/index";
	}

	// 조회수순 갤러리 리스트 페이징 ajax
	@ResponseBody
	@GetMapping("/gallery/pageByHits/{page_index}")
	public List<Gallery_index> gallery_view_hits(@PathVariable("page_index") int page_index, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Gallery_index> gallery_list = galleryService.getListByHits(page_index);
		if(gallery_list != null && gallery_list.size() > 0) {
			gallery_list = galleryService.setCommentCount(gallery_list, member_idx);
		}
		return gallery_list;
	}

	// 갤러리 해쉬태그 검색 페이징 처리
	@GetMapping("/gallery/searchByTag/{hashtag}/{page_index}")
	public String getListByHashTag(@PathVariable("hashtag") String hashtag, @PathVariable("page_index") int index, Model model) {
		List<Gallery_index> list = galleryService.getGalleryByHashTag(hashtag.toLowerCase(), index);
		for(int i=0 ; i<list.size() ; i++) {
			Gallery_index g = list.get(i);
			g.setHashtag2(g.getHashtag().split(","));
		}
		model.addAttribute("gallery_list", list);
		return "gallery/index";
	}

	// 갤러리 검색 (닉네임, 제목, 내용, 해쉬태그 검색)
	@GetMapping("/gallery/tag_search")
	public String getListByKeyword(@RequestParam("keyword") String keyword, @RequestParam("type") String type, Model model) {
		List<Gallery_index> list = null;
		if(keyword == null || keyword.trim().length() == 0 || type == null || type.trim().length() == 0) {
			System.out.println("입력값 필요!");
			return "error";
		}
		
		if(type.equals("writer")) {
			list = galleryService.getListByNickname(keyword);
		} else if(type.equals("title")) {
			list = galleryService.getListBySubject(keyword);
		} else if(type.equals("contents")) {
			list = galleryService.getListByContents(keyword);
		} else if(type.equals("tag")) {
			list = galleryService.getListByHashTag(keyword.toLowerCase());
		} else {
			System.out.println("타입 오류");
			return "error";
		}
		
		for(int i=0 ; i<list.size() ; i++) {
			Gallery_index g = list.get(i);
			g.setHashtag2(g.getHashtag().split(","));
		}
		model.addAttribute("gallery_list", list);
		return "gallery/index";
	}

	// 게시글 좋아요버튼 클릭시 ajax
	@ResponseBody
	@GetMapping("/gallery/{diary_idx}/likes")
	public String like_qna(@PathVariable("diary_idx") int diary_idx, HttpSession session) {
		StringBuffer result = new StringBuffer();
		if(session.getAttribute("idx") == null) {
			return "error";
		}

		int member_idx = (int) session.getAttribute("idx");
		Id_likes likes = galleryService.findLikes(member_idx, diary_idx);
		if(likes == null) {
			// 좋아요를 안누른 게시글인 경우
			result.append("plus:");
			likes = new Id_likes(member_idx, diary_idx);
			galleryService.saveLikes(likes);
			// 좋아요 +1 업데이트
			dashboardService.updateLikes(diary_idx);
		} else {
			// 이미 좋아요를 누른 게시글인 경우
			result.append("minus:");
			galleryService.deleteIdLikes(member_idx, diary_idx);
			// 좋아요 -1 업데이트
			dashboardService.updateLikesM(diary_idx);
		}

		Infant_diary diary = dashboardService.findDiary(diary_idx);
		if(diary == null) {
			return "error2";
		}

		result.append(diary.getLikes());
		return result.toString();
	}

	// 댓글 좋아요 클릭시
	@ResponseBody
	@GetMapping("/gallery/commentLikes/{comment_idx}")
	public String like_comment_qna(@PathVariable("comment_idx") int comment_idx, HttpSession session) {
		if(session.getAttribute("idx") == null) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		int member_idx = (int) session.getAttribute("idx");
		Id_comment_likes likes = galleryService.findCommentLikes(member_idx, comment_idx);
		if(likes == null) {
			result.append("plus:");
			galleryService.plusCommentLikes(member_idx, comment_idx);
		} else {
			result.append("minus:");
			galleryService.minusCommentLikes(member_idx, comment_idx);
		}
		Id_comment comment = galleryService.findComment(comment_idx);
		result.append(comment.getLikes());
		return result.toString();
	}

	@ResponseBody
	@GetMapping("/gallery/total_count")
	public int get_gallery_count() {
		return boardService.getGalleryCount();
	}

	// 갤러리 글 작성 get (기능 삭제)
	@GetMapping("/gallery/upload")
	public String board_write() {
		return "gallery/board_write";
	}

//	// 갤러리 글 작성 post (기능 삭제)
//	@ResponseBody
//	@PostMapping("/gallery/upload")
//	public String board_write_post(@RequestParam("title") String title, @RequestParam("content") String content,
//			@RequestParam(value="tags", required=false) String tags, @RequestParam("upload_image") MultipartFile upload_image, HttpSession session) throws IOException {
//		InputStream inputStream = null;
//		BufferedImage originalImage = null;
//		BufferedImage thumbnail = null;
//		try {
//			inputStream = upload_image.getInputStream();
//			originalImage = ImageIO.read(inputStream);
//			int width = originalImage.getWidth();
//			int height = originalImage.getHeight();
//
//			// 화면 넓이를 가져와서 화면 
//			thumbnail = Thumbnails.of(originalImage).size(365, ((365*height)/width)).outputQuality(1.0f).asBufferedImage();
//
//			// 원본 이미지 업로드
//			amazonS3Service.putThumbnailBI("community/gallery", file_name, extension, originalImage);
//			// 썸네일용 이미지 추가 업로드
//			amazonS3Service.putThumbnailBI("community/gallery/gallery_thumbnail", file_name, extension, thumbnail);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "3";
//		} finally {
//			if(inputStream != null) {
//				inputStream.close();
//				inputStream = null;
//			}
//			if(originalImage != null) {
//				originalImage.flush();
//				originalImage = null;
//			}
//			if(thumbnail != null) {
//				thumbnail.flush();
//				thumbnail = null;
//			}
//		}
//	}

	// 갤러리 특정글 클릭
	@GetMapping("/gallery/view/{diary_idx}")
	public String board_view(@PathVariable("diary_idx") int diary_idx, Model model, HttpSession session) {
		int member_idx = (int)session.getAttribute("idx");
		Infant_diary diary = dashboardService.findDiary(diary_idx);
		List<Comment_check> comment_list;
		if(diary != null) {
			diary.setId_files(dashboardService.findIdFileByDiary_idx(diary_idx));
			comment_list = galleryService.getListCommentCheck(diary_idx);
			model.addAttribute("gallery", diary);
			
			String contents = diary.getContents();
			if(contents != null && contents.trim().length() > 0) {
	        	model.addAttribute("parsing_contents", boardService.parsingHtml(contents));
	        }
		} else {
			return "error";
		}
		
		

		String hashtag = diary.getHashtag();
		if(hashtag != null) {
			model.addAttribute("hashtag", hashtag.split(","));
		}

		if(comment_list != null && comment_list.size() > 0) {
			comment_list = setLikesCheckAndDate(comment_list, member_idx);
			model.addAttribute("comment_list", comment_list);
		}

		if(diary.getMember().getIdx() != (int)session.getAttribute("idx")) {
			dashboardService.updateHits(diary_idx);
		}

		Id_likes bl = galleryService.findLikes((int)session.getAttribute("idx"), diary_idx);
		if(bl != null) {
			model.addAttribute("likes_check", 1);
		}
		return "gallery/board_view"; 
	}
	
	// 댓글 더보기시 댓글 페이징 ajax
	@ResponseBody
	@GetMapping("/gallery/{gallery_idx}/commentPaging/{page_index}")
	public List<Id_comment> getCommentListPaging(@PathVariable("gallery_idx") int gallery_idx, @PathVariable("page_index") int page_index) {
		List<Id_comment> list = galleryService.getCommentListPaging(gallery_idx, page_index);
		for(int i=0 ; i<list.size(); i++) {
			try {
				list.get(i).setDate_created(galleryService.setViewDate(list.get(i).getDate_created()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	@GetMapping("/gallery/modify/{gallery_idx}")
	public String modify_gallery(@PathVariable("gallery_idx") int gallery_idx, Model model, HttpSession session) {
		Infant_diary diary = dashboardService.findDiary(gallery_idx);
		if(diary == null) {
			return "error";
		}

		if(diary.getMember().getIdx() != (int)session.getAttribute("idx")) {
			return "error";
		}

		model.addAttribute("gallery", diary);
		return "gallery/board_update";
	}

	@ResponseBody
	@PostMapping("/gallery/modify/{diary_idx}")
	public String modify_gallery_post(@PathVariable("diary_idx") int diary_idx, @RequestParam("title") String title,
			@RequestParam("content") String content, @RequestParam(value="tags", required=false) String tag, 
			@RequestParam("upload_image") MultipartFile upload_image, Model model, HttpSession session) throws IOException {
		Infant_diary diary = dashboardService.findDiary(diary_idx);
		if(diary == null) {
			return "1";
		}

		if(diary.getMember().getIdx() != (int)session.getAttribute("idx")) {
			return "2";
		}

		String now_time = memberService.set_now_time();
		if(tag != null && tag.length() > 0) {
			dashboardService.updateDiaryAtGallery2(diary_idx, title, content, now_time, tag);
		} else {
			dashboardService.updateDiaryAtGallery(diary_idx, title, content, now_time);
		}

		if(upload_image != null && upload_image.isEmpty() == false) {
			// 갤러리는 첨부파일이 1개
			List<Id_file> bf = dashboardService.findIdFileByDiary_idx(diary_idx);
			String originalName = upload_image.getOriginalFilename();
			String serverFileName = memberService.getUniqFileName(originalName);
			String file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/gallery/" + serverFileName;
			InputStream inputStream = null;

			Id_file represent = null;
			// s3에서 기존 파일 삭제후 새 파일 업로드
			for(int i=0 ; i < bf.size() ; i++) {
				if(bf.get(i).getRepresent() == '1') {
					represent = bf.get(i);
				}
			}
			amazonS3Service.deleteThumbnail("community/gallery", represent.getServer_filename());
			try {
				inputStream = upload_image.getInputStream();
				amazonS3Service.putThumbnail("community/gallery", serverFileName, inputStream, amazonS3Service.getMetadata(upload_image));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "3";
			} finally {
				if(inputStream != null) {
					inputStream.close();
				}
			}
			boardService.updateBf(bf.get(0).getIdx(), originalName, serverFileName, file_url, (int)upload_image.getSize());

		}
		return "4";
	}

	@ResponseBody
	@GetMapping("/gallery/delete/{diary_idx}")
	public String delete_gallery(@PathVariable("diary_idx") int diary_idx, HttpSession session) {
		Infant_diary diary = dashboardService.findDiary(diary_idx);
		if(diary == null) {
			return "1";
		}

		if(diary.getMember().getIdx() != (int)session.getAttribute("idx")) {
			return "2";
		}
		List<Id_file> bf = dashboardService.findIdFileByDiary_idx(diary_idx);

		if(bf != null && bf.size() > 0) {
			// 썸네일 삭제
			amazonS3Service.deleteThumbnail("community/gallery", bf.get(0).getServer_filename());	
			dashboardService.deleteIdFileByDiary(diary_idx);
		}

		List<Id_comment> comments = diary.getComments();
		if(comments != null && comments.size() > 0) {
			dashboardService.deleteCommentsByDiary(diary_idx);
		}
		dashboardService.delete_diary(diary_idx);
		return "3";
	}

	// 갤러리 댓글 작성
	@ResponseBody
	@PostMapping("/gallery/{diary_idx}/comment_write")
	public String comment_write(@PathVariable("diary_idx") int diary_idx, @RequestParam("contents") String contents, HttpSession session) {
		Id_comment comment = new Id_comment();
		comment.setDiary_idx(diary_idx);
		comment.setContents(contents);
		comment.setDate_created(memberService.set_now_time());
		comment.setMember(memberService.findByIdx((int)session.getAttribute("idx")));
		comment = dashboardService.saveComment(comment);

		int writer = dashboardService.findDiary(diary_idx).getMember().getIdx();
		int requester = (int)session.getAttribute("idx");

		if(requester != writer) {
			Alert alert = new Alert();
			alert.setEvent_type('3');	// 갤러리 알람 이벤트
			alert.setRecipient(writer);
			alert.setRequester(requester);
			alert.setRequest_date(memberService.set_now_time());
			alert = alarmService.insert(alert);

			Alert_board ab = new Alert_board();
			ab.setIdx(alert.getIdx());
			ab.setBoard_idx(diary_idx);
			ab.setComment_idx(comment.getIdx());
			alarmService.saveAlert_board(ab);
		}
		
		return "1";
	}

	// 댓글 수정 ajax
	@ResponseBody
	@PostMapping("/gallery/commentModify/{comment_idx}/{diary_idx}")
	public Comment_data modify_comment(@RequestParam("contents") String contents, @PathVariable("comment_idx") int comment_idx,
			@PathVariable("diary_idx") int diary_idx, HttpSession session) {
		Id_comment comment = dashboardService.findComment(comment_idx);
		Comment_data data = new Comment_data();
		data.setComments(null);
		if(session.getAttribute("idx") == null) {
			data.setResult(1);
			return data;
		}
		if(comment.getMember().getIdx() != (int)session.getAttribute("idx")) {
			data.setResult(2);
			return data;
		}
		int member_idx = (int) session.getAttribute("idx");
		galleryService.updateComment(comment_idx, contents, memberService.set_now_time());
		List<Comment_check> list = galleryService.getListCommentCheck(diary_idx);
		data.setComments(setLikesCheckAndDate(list, member_idx));
		data.setResult(3);
		return data;
	}

	public List<Comment_check> setLikesCheckAndDate(List<Comment_check> list, int member_idx) {
		for(int i=0 ; i<list.size() ; i++) {
			Id_comment_likes likes = galleryService.findCommentLikes(member_idx, list.get(i).getIdx());
			if(likes != null) {
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

	// 갤러리 댓글 리스트
	@ResponseBody
	@GetMapping("/gallery/{diary_idx}/comment_list")
	public List<Id_comment> get_comment_list(@PathVariable("diary_idx") int diary_idx, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Id_comment> comment_list = dashboardService.findCommentsByDiary(diary_idx);
		for(int i=0 ; i<comment_list.size() ; i++) {
			Id_comment_likes likes = dashboardService.findCommentLikes(member_idx, comment_list.get(i).getIdx());
			if(likes != null) {
				comment_list.get(i).setLikes_check(1);
			}
			try {
				comment_list.get(i).setDate_created(galleryService.setViewDate(comment_list.get(i).getDate_created()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return comment_list;
	}

	@ResponseBody
	@GetMapping("/gallery/deleteComment/{comment_idx}")
	public String delete_comment(@PathVariable("comment_idx") int comment_idx) {
		Id_comment comment = dashboardService.findComment(comment_idx);
		if(comment == null) {
			return "1";
		}
		dashboardService.deleteComment(comment_idx);
		return "2";
	}

	// 갤러리 댓글 알림을 통해 갤러리글에 접근할 때
	@GetMapping("/gallery/alert/{diary_idx}/{comment_idx}/{alarm_idx}")	
	public String read_qna_alert(@PathVariable("diary_idx") int diary_idx, @PathVariable("comment_idx") int comment_idx,
			@PathVariable("alarm_idx") int alarm_idx, Model model, HttpSession session) {

		// 알림 db 삭제
		if(alarmService.findAlert(alarm_idx) != null) {
			alarmService.updateReadChk(alarm_idx);
		}

		Board gallery = boardService.findOne(diary_idx);
		List<Comment> comment_list;
		if(gallery != null) {
			gallery.setBoard_files(boardService.findBf(diary_idx));
			comment_list = gallery.getComments();
			model.addAttribute("gallery", gallery);
		} else {
			return "error";
		}

		model.addAttribute("alert_comment_idx", comment_idx);
		String hashtag = gallery.getHashtag();
		if(hashtag != null) {
			model.addAttribute("hashtag", hashtag.split(","));
		}

		if(comment_list != null && comment_list.size() > 0) {
			model.addAttribute("comment_list", comment_list);
		}

		if(gallery.getMember().getIdx() != (int)session.getAttribute("idx")) {
			boardService.updateHits(diary_idx);
		}

		return "gallery/board_view"; 
	}

	// 커뮤니티 작성자 썸네일 클릭시 회원정보
	@GetMapping("/community/view_member_info/{member_idx}/{type}/{page_index}")
	public String viewMemberInfoPaging(@PathVariable("member_idx") int member_idx, @PathVariable("page_index") int page_index,
			@PathVariable("type") String type, Model model, HttpSession session) {
		int session_idx = (int) session.getAttribute("idx");
		boolean check_auth = false;

		if(member_idx == session_idx) {
			check_auth = true;
		}

		// type은 board 또는 gallery
		if(!type.equals("board") && !type.equals("gallery")) {
			System.out.println("type error");
			return "error";
		}
		model.addAttribute("type", type);

		Member member = memberService.findByIdx(member_idx);
		if(member != null) {
			model.addAttribute("member", member);
		} else {
			return "error";
		}
		List<Member_infant> mi = groupService.findByMember_idx(member_idx);
		if(mi != null && mi.size() > 0) {
			Infant infant = mi.get(0).getInfant();
			model.addAttribute("infant", infant);	// 임시로 첫번째 아이만 전달
			try {
				model.addAttribute("infant_age", infantService.getInfantYear(infant.getBirth()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int total_write = 0;
		
		List<Gallery_index> gallery = galleryService.getGalleryByMember_idx(member_idx, page_index);
		int comment_count = dashboardService.countComment(member_idx);
		if(gallery != null && gallery.size() > 0) {
			model.addAttribute("gallery", gallery);
			total_write += gallery.size();
		}

		List<Board_index> board = boardService.getListByMember_idx(member_idx, page_index);
		int board_comment_count = boardService.countComment(member_idx);

		model.addAttribute("total_comment_count", comment_count + board_comment_count);

		if(board != null && board.size() > 0) {
			System.out.println("total member board size : " + boardService.getCountMemberBoard(member_idx));
			
			board = boardService.setBoardIndex(board);
			model.addAttribute("board", board);
			total_write += board.size();
		}
		
		model.addAttribute("total_write", total_write);

		Member_mng mng = mngService.findOne(member_idx);
		if(mng != null) {
			model.addAttribute("grade", mng.getGrade());
			model.addAttribute("point", mng.getPoint());
			if(mng.getIntroduction() != null && mng.getIntroduction().length() > 0) {
				model.addAttribute("introduction", mng.getIntroduction());
			}
		}

		Member_group group = groupService.findMember_group(member_idx);
		if(group != null) {
			List<Member_group> group_members = groupService.findByGroup_idx(group.getGroup_idx());
			Map<String, String> group_members_response = new HashMap<String, String>();

			for(int i=0 ; i<group_members.size() ; i++) {
				Member m = memberService.findByIdx(group_members.get(i).getMember_idx());
				if(m.getIdx() == session_idx) {
					check_auth = true;
				}
				if(group_members.get(i).getAuthority() == '1') {
					model.addAttribute("group_master", m.getNickname());
					if(m.getThumbnail() != null && m.getThumbnail().length() > 0) {
						model.addAttribute("group_master_thumb", m.getThumbnail());
					}
				} else {
					group_members_response.put(m.getNickname(), m.getThumbnail());
				}
			}

			model.addAttribute("group_members", group_members_response);
		}

		model.addAttribute("totalLikes", boardService.getTotalLikes(member_idx));
		model.addAttribute("check_auth", check_auth);
		model.addAttribute("page_index", page_index);

		int size = boardService.getCountMemberBoard(member_idx);
		int i = 0;
		if(size % 10 > 0) {
			i = ((int)(size / 20)) + 1;
		} else {
			i = (int)(size / 20);
		}

		List<Integer> page_list = new ArrayList<Integer>();
		for(int index = 1 ; index <= i ; index++) {
			page_list.add(index);
		}
		model.addAttribute("page_size", page_list);
		return "user/view";
	}

	/*
	// 마이페이지 게시글 작성 갯수 ajax
	@ResponseBody
	@GetMapping("/mypage/view_member_info/{member_idx}/gallery_count")
	public int viewMemberInfoBoardCount(@PathVariable("member_idx") int member_idx) {
		return boardService.getCountMemberBoard(member_idx);
	}
	
	@ResponseBody
	@GetMapping("/gallery/{gallery_idx}/likes_view")
	public List<GalleryLikesInfo> gallery_likes_view(@PathVariable("gallery_idx") int gallery_idx) {
		// member_idx, thumbnail, nickname 3개 필드 필요
		List<GalleryLikesInfo> info = galleryService.findLikesByGallery(gallery_idx);
		if(info.size() > 0) {
			return info;
		} else {
			return null;
		}
	}*/

}
