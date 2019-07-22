package com.example.demo2.phrase;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhraseCount {

	private String id;
	
	private String phrase;
	
	private long count;
	
	private Date start;
	
	private Date end;
}
