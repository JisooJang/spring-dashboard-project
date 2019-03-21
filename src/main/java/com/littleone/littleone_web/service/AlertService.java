package com.littleone.littleone_web.service;

import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Alert_board;

public interface AlertService {
	public Alert insert(Alert alert);
	public void deleteAlert(int idx);
	public Alert_board saveAlert_board(Alert_board alert_board);
	public Alert_board findByComment_idx(int comment_idx);
	public Alert findAlert(int idx);
	public Alert duplicate_check(int requester, int recipient);
	public int findRecipient(int idx);
	public int findGroupAlertIdx(int requester, int recipient);
	public int findRequester(int idx);
	public void deleteByRecipient(int recipient);
	public int updateReadChk(int idx);
	public void readAll(int recipient);
}
