package com.example.demo1.events;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookEvt {

	private String title;
	private Collection<Autor> autors;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Autor {
		private String name;
	}
}
