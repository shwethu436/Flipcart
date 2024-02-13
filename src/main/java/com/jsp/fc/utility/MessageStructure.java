package com.jsp.fc.utility;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageStructure {
	
	private String to;
	private String subject;
	private String text;
	private Date sentDate;

}
