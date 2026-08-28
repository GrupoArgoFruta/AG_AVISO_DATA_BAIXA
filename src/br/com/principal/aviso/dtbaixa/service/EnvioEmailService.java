package br.com.principal.aviso.dtbaixa.service;

import java.math.BigDecimal;
import java.util.List;

import com.sankhya.util.BigDecimalUtil;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class EnvioEmailService {

	private static final String EMAIL_COPIA = "comprovante.pagamento@argofruta.com";

	public void enviarEmail(String titulo, String mensagem, List<String> emailsParceiro) throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			JapeWrapper filaDAO = JapeFactory.dao(DynamicEntityNames.FILA_MSG);

			// 1. Envia pra cada e-mail do parceiro
			for (String emailParceiro : emailsParceiro) {
				filaDAO.create()
						.set("EMAIL", emailParceiro.trim())
						.set("CODCON", BigDecimal.ZERO)
						.set("STATUS", "Pendente")
						.set("TIPOENVIO", "E")
						.set("MAXTENTENVIO", BigDecimalUtil.valueOf(3))
						.set("ASSUNTO", titulo)
						.set("MENSAGEM", mensagem.toCharArray())
						.save();
			}

			// 2. Cópia pra você
			filaDAO.create()
					.set("EMAIL", EMAIL_COPIA)
					.set("CODCON", BigDecimal.ZERO)
					.set("STATUS", "Pendente")
					.set("TIPOENVIO", "E")
					.set("MAXTENTENVIO", BigDecimalUtil.valueOf(3))
					.set("ASSUNTO", "[CÓPIA] " + titulo)
					.set("MENSAGEM", mensagem.toCharArray())
					.save();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			JapeSession.close(hnd);
		}
	}
}