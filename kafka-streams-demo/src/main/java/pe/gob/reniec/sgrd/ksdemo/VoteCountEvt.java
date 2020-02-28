package pe.gob.reniec.sgrd.ksdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCountEvt {

	private String id;
	private String name;
	private Long total;
}
