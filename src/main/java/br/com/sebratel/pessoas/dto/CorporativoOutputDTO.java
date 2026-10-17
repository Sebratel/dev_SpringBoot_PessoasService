package br.com.sebratel.pessoas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CorporativoOutputDTO {
    InsigniaOutputDTO insignia;
    boolean corporativo;
}
