package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Alert_board;
import com.littleone.littleone_web.repository.AlertRepository;
import com.littleone.littleone_web.repository.Alert_boardRepository;

@Service
public class AlertServiceImpl implements AlertService {

	@Autowired
	private AlertRepository alertRepository;
	
	@Autowired
	private Alert_boardRepository abRepository;
	
	@Override
	public void deleteAlert(int idx) {
		// TODO Auto-generated method stub
		alertRepository.delete(idx);
	}

	@Override
	public Alert_board saveAlert_board(Alert_board alert_board) {
		// TODO Auto-generated method stub
		return abRepository.save(alert_board);
	}

	@Override
	public Alert_board findByComment_idx(int comment_idx) {
		// TODO Auto-generated method stub
		return abRepository.findByComment_idx(comment_idx);
	}

	@Override
	public Alert insert(Alert alert) {
		// TODO Auto-generated method stub
		return alertRepository.save(alert);
	}

	@Override
	public Alert findAlert(int idx) {
		// TODO Auto-generated method stub
		return alertRepository.findOne(idx);
	}
	
	@Override
	public Alert duplicate_check(int requester, int recipient) {
		// TODO Auto-generated method stub
		return alertRepository.duplicate_check(requester, recipient);
	}
	
	@Override
	public int findRecipient(int idx) {
		// TODO Auto-generated method stub
		return alertRepository.findRecipient(idx);
	}
	
	@Override
	public int findGroupAlertIdx(int requester, int recipient) {
		// TODO Auto-generated method stub
		return alertRepository.findIdx(requester, recipient);
	}
	
	@Override
	public int findRequester(int idx) {
		// TODO Auto-generated method stub
		return alertRepository.findRequester(idx);
	}

	@Override
	public void deleteByRecipient(int recipient) {
		// TODO Auto-generated method stub
		alertRepository.deleteByRecipient(recipient);
	}

	@Override
	public int updateReadChk(int idx) {
		// TODO Auto-generated method stub
		return alertRepository.updateReadChk(idx);
	}

	@Override
	public void readAll(int recipient) {
		// TODO Auto-generated method stub
		alertRepository.readAll(recipient);
	}
}
