package com.example.demo1.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBookForErrorEvt {

	private Boolean error;
}
