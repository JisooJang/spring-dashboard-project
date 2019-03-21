package com.littleone.littleone_web.api_response;

public class KaKaoProfileResponse {
	private int id;
	private KakaoProperties properties;
	private KakaoAccount kakao_account;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public KakaoProperties getProperties() {
		return properties;
	}
	public void setProperties(KakaoProperties properties) {
		this.properties = properties;
	}
	public KakaoAccount getKakao_account() {
		return kakao_account;
	}
	public void setKakao_account(KakaoAccount kakao_account) {
		this.kakao_account = kakao_account;
	}
}
