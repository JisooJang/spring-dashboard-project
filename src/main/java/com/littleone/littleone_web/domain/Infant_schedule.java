package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "infant_schedule")
public class Infant_schedule {
    @Id
    @Column(name = "idx")
    @GeneratedValue
    private int idx;

    @OneToOne
    @JoinColumn(name = "infant_idx")
    private Infant infant;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

	@Column(name="event_date_start", nullable=false)
	private String event_date_start;
	
	@Column(name="event_date_end", nullable=false)
	private String event_date_end;

    @Column(name = "date_created", nullable = false)
    private String date_created;

    @Column(name = "date_updated", nullable = true)
    private String date_updated;

    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "event_type", nullable = false)
    private char event_type;
    
    @Transient
    private String interval;	// 이벤트 시작시간과 종료시간 차이를 담을 변수. DB 연동 x

    public Infant_schedule(int idx, Infant infant, Member member, String event_date_start, String event_date_end,
                           String date_created, String date_updated, String title, char event_type) {
        super();
        this.idx = idx;
        this.infant = infant;
        this.member = member;
        this.event_date_start = event_date_start;
        this.event_date_end = event_date_end;
        this.date_created = date_created;
        if (date_updated != null) {
            this.date_updated = date_updated;
        }
        this.title = title;
        this.event_type = event_type;
        
        // interval 계산후 값 저장 (interval값 제한 필요)
        setInterval();
        
    }

    public Infant_schedule(Infant infant, Member member, String event_date_start, String event_date_end,
                           String date_created, String date_updated, String title, char event_type) {
        super();
        this.infant = infant;
        this.member = member;
        this.event_date_start = event_date_start;
        this.event_date_end = event_date_end;
        this.date_created = date_created;
        if (date_updated != null) {
            this.date_updated = date_updated;
        }
        this.title = title;
        this.event_type = event_type;
        
        // interval 계산후 값 저장 (interval값 제한 필요)
        setInterval();
    }
    
    

    public Infant_schedule(String event_date_start, String event_date_end, String title, char event_type,
			String interval) {
		super();
		this.event_date_start = event_date_start;
		this.event_date_end = event_date_end;
		this.title = title;
		this.event_type = event_type;
		this.interval = interval;
		
		// interval 계산후 값 저장 (interval값 제한 필요)
        setInterval();
	}

	public Infant_schedule() {
        super();
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public char getEvent_type() {
        return event_type;
    }

    public void setEvent_type(char event_type) {
        this.event_type = event_type;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public Infant getInfant() {
        return infant;
    }

    public void setInfant(Infant infant) {
        this.infant = infant;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getEvent_date_start() {
		return event_date_start;
	}

	public void setEvent_date_start(String event_date_start) {
		this.event_date_start = event_date_start;
	}

	public String getEvent_date_end() {
		return event_date_end;
	}

	public void setEvent_date_end(String event_date_end) {
		this.event_date_end = event_date_end;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	@Override
    public String toString() {
        return "Infant_schedule{" +
                "idx=" + idx +
                ", infant=" + infant +
                ", member=" + member +
                ", event_date_start='" + event_date_start + '\'' +
                 ", event_date_end='" + event_date_end + '\'' +
                ", date_created='" + date_created + '\'' +
                ", date_updated='" + date_updated + '\'' +
                ", title='" + title + '\'' +
                ", event_type=" + event_type;
    }
	
	public void setInterval() {
		int start_y = Integer.parseInt(event_date_start.substring(0, 4));
		int start_mo = Integer.parseInt(event_date_start.substring(5, 7));
		int start_d = Integer.parseInt(event_date_start.substring(8, 10));
        int start_h = Integer.parseInt(event_date_start.substring(11, 13));
        int start_m = Integer.parseInt(event_date_start.substring(14, 16));
        
        int end_y = Integer.parseInt(event_date_end.substring(0, 4));
        int end_mo = Integer.parseInt(event_date_end.substring(5, 7));
        int end_d = Integer.parseInt(event_date_end.substring(8, 10));
        int end_h = Integer.parseInt(event_date_end.substring(11, 13));
        int end_m = Integer.parseInt(event_date_end.substring(14, 16));
        
        StringBuffer temp = new StringBuffer();
        if(start_y < end_y) {
        	temp.append(end_y - start_y + "년 ");
        } else {
        	if(start_mo < end_mo) {
            	// 종료 월이 더 뒤일때
            	temp.append((end_mo - start_mo) + "월 ");
            } else {
            	if(start_d < end_d) {
            		// 종료 일이 더 뒤일때
            		temp.append((end_d - start_d) + "일 "); 
            	} else {
            		if(start_h < end_h) {
            			// 종료 시간이 더 뒤일때
            			temp.append((end_h - start_h) + "시간");
            		}
            	}
            }
        }
            
        this.interval = temp.toString();
	}
}

