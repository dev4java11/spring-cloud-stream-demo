package com.example.demo1.book;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo1.autor.Autor;
import com.example.demo1.events.BookCreatedEvt;
import com.example.demo1.events.CreateBookEvt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	private String id;
	private String title;
	private Collection<Autor> autors;
	
	public Book(CreateBookEvt evt) {
		this.id = UUID.randomUUID().toString();
		this.title = evt.getTitle();
		this.autors = evt.getAutors()
				.stream()
				.map(aut -> new Autor(UUID.randomUUID().toString(), aut.getName()))
				.collect(Collectors.toList());
	}
	
	public BookCreatedEvt bookCreated() {
		return new BookCreatedEvt(this.id);
	}
}
