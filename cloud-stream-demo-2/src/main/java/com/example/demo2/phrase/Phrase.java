package com.example.demo2.phrase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Phrase {

	private String id;
	private String phrase;
	private String user;
	private String username;
}
