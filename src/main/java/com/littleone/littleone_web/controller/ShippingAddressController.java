package com.littleone.littleone_web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.littleone.littleone_web.command.MemberRegistRequest;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Shipping_address;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.ShippingAddressService;

@Controller
public class ShippingAddressController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ShippingAddressService addressService;

	// 배송지 목록
	/*@RequestMapping(value="/address_list", method=RequestMethod.GET)	// 배송지 리스트 불러오기
	public String list_view(HttpSession session, Model model) {
		int member_idx = (int) session.getAttribute("idx");
		List<Shipping_address> list = addressService.findByMember_idx(member_idx);
		String name = memberService.findByIdx(member_idx).getName();
		if(name != null && name.length() > 0) {
			model.addAttribute("member_name", name);
		}
		if(list.size() > 0) {
			model.addAttribute("address_list", list);
		} 
		return "address/address_view";
	}*/
	
	/*@ResponseBody
	@RequestMapping(value="/get_address_list", method=RequestMethod.GET)
	public List<Shipping_address> get_list(HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		List<Shipping_address> list = addressService.findByMember_idx(member_idx);
		if(list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/count_address_list", method=RequestMethod.GET)
	public int count_address_list(HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		if(session.getAttribute("idx") != null) {
			return addressService.count_by_member_idx(member_idx);
		} else {
			System.out.println("세션이 존재하지 않음");
			return 0;
		}
	}
	
	@ResponseBody	// 베송지 추가 ajax
	@RequestMapping(value="/add_address", method=RequestMethod.POST)	// 배송지 추가하기
	public String add_address(@ModelAttribute Shipping_address address, HttpSession session) {
		//1. 입력값 검증
		if(addressService.validation_check(address) == false) {
			System.out.println("입력값 검증 실패");
			return "failed";
		}
		int count = addressService.count_by_member_idx((int)session.getAttribute("idx"));
		if(count >= 3) {
			System.out.println("배송지 추가는 최대 3개까지만 가능합니다.");
			return "failed";
		} else if(count == 0) {
			// 최초 배송지 등록
			address.setMember_idx((int)session.getAttribute("idx"));
			address.setDefault_check('y');
			address.setMember_info('n');
			if(addressService.insert(address) != null) {
				return "success";
			} else {
				return "failed";
			}
		} else { 
			address.setMember_idx((int)session.getAttribute("idx"));
			address.setDefault_check('n');
			address.setMember_info('n');
			if(addressService.insert(address) != null) {
				return "success";
			} else {
				return "failed";
			}
		}
	}
	
	@ResponseBody	// 배송지 수정 get ajax
	@RequestMapping(value="/modify_address/{address_idx}", method=RequestMethod.GET)
	public Shipping_address modify_adress_get(@PathVariable("address_idx") String address_idx) {
		Shipping_address address = addressService.findByIdx(Integer.parseInt(address_idx));
		// 모델에 추가, 또는 ajax 응답으로 객체 전달?
		if(address != null) { return address; }
		else { return null; }
	}
	
	@ResponseBody	// 배송지 수정 submit ajax
	@RequestMapping(value="/modify_address/{address_idx}", method=RequestMethod.POST)
	public String modify_address(@ModelAttribute Shipping_address address, @PathVariable("address_idx") String address_idx,
			HttpSession session) {
		// 1. 입력값 검증
		if(addressService.validation_check(address) == false) {
			System.out.println("입력값 검증 실패");
			return "failed";
		}
		int result = addressService.updateAddress(Integer.parseInt(address_idx), address.getRecipient_name(), address.getRecipient_phone(),
				address.getRecipient_phone2(), address.getAddress_name(), address.getAddress1(), address.getAddress2(), address.getZipcode());
		if(result == 1) {
			return "success";
		} else {
			// DB rollback
			return "failed";
		}
	}
	
	@ResponseBody	// 회원 기본 주소 정보 불러오기 ajax
	@RequestMapping(value="/load_default_address", method=RequestMethod.GET)
	public Shipping_address load_default_address(HttpSession session) {
		String member_idx = (String) session.getAttribute("idx");
		if(member_idx != null) {
			Shipping_address address = addressService.findMember_info_true(Integer.parseInt(member_idx));
			return address;
		} else {
			System.out.println("세션이 존재하지 않습니다.");
			return null;
		}
	}
	
	@ResponseBody	// 배송지 삭제 ajax
	@RequestMapping(value="/delete_address/{address_idx}", method=RequestMethod.GET)
	public String delete_address(@PathVariable("address_idx") String address_idx, HttpSession session) {
		try {
			if(addressService.check_default_check(Integer.parseInt(address_idx))) {
				System.out.println("기본배송지는 삭제 불가");
				return "default_failed";
			}
		addressService.delete(Integer.parseInt(address_idx));
		} catch(EmptyResultDataAccessException e) {
			System.out.println("존재하지 않는 idx값입니다.");
			return "failed";
		} catch(Exception e) {
			return "failed";
		}
		return "success";
	}
	
	@ResponseBody	// 기본 배송지로 설정 ajax
	@RequestMapping(value="/set_default_address/{address_idx}", method=RequestMethod.GET)
	public String set_default_address(@PathVariable("address_idx") String address_idx, HttpSession session) {
		// 기존에 기본 배송지로 설정된 행의 default_check 컬럼을 n으로 수정
		// address_idx 행을 찾아 default_check y
		int idx = (int) session.getAttribute("idx");
		if(session.getAttribute("idx") != null) {
			// 배송지 컬럼이 1개라고 존재하고, default_check가 y인 행이 1개라도 있을경우 조건 추가
			if(addressService.count_default(idx) > 0) {
				int result1 = addressService.cancel_default_check(idx);	// 기존의 기본배송지를 n으로 설정
				int result2 = addressService.update_default_check_y(Integer.parseInt(address_idx));		// 선택된 행을 default_check y	
				if(result1 == 1 && result2 == 1) {
					return "success";
				} else {
					// DB rollback
					return "failed";
				}
			} else {
				// 배송지는 존재하는데 기본배송지가 없거나, 배송지 추가가 한번도 안된경우
				System.out.println("배송지는 존재하는데 기본배송지가 없거나, 배송지 추가가 한번도 안된경우");
				return "failed";
			}		
			
		} else {
			System.out.println("세션이 좋재하지 않습니다.");
			return null;
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/load_default_info", method=RequestMethod.GET)
	public Member load_default_info(HttpSession session) {
		int idx = (int) session.getAttribute("idx");
		if(session.getAttribute("idx") != null) {
			Member member = memberService.findByIdx(idx);
			return member;
		} else {
			System.out.println("세션이 좋재하지 않습니다.");
			return null;
		}
		
	}*/
}


