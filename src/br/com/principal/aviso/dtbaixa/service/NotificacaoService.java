package br.com.principal.aviso.dtbaixa.service;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;

public class NotificacaoService {
	public void notificarUsuarios(BigDecimal coduser, String obs, String titulo)
			throws MGEModelException {

		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {

			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);

			sql.appendSql("SELECT CODUSU,NOMEUSU FROM TSIUSU \r\n"
					+ "WHERE CODUSU  IN (0)");

			sql.setNamedParameter("CODUSU", coduser);
			rset = sql.executeQuery();

			while (rset.next()) {

				notifUsu(rset.getBigDecimal("CODUSU"), obs, titulo);
			}
		} catch (Exception e) {
			MGEModelException.throwMe(e);
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}

}
	public void notifUsu( BigDecimal codUsu, String obs, String titulo)
			throws MGEModelException {

		ServiceContext sc = new ServiceContext(null);
		sc.setAutentication(AuthenticationInfo.getCurrent());
		sc.makeCurrent();
		try {
			SPBeanUtils.setupContext(sc);
		} catch (Exception e) {
			e.printStackTrace();
			MGEModelException.throwMe(e);
		}
		JapeWrapper avisoDAO = JapeFactory.dao("AvisoSistema");
		try {
			if (codUsu != null) {
				@SuppressWarnings("unused")
				DynamicVO avisoVO = (DynamicVO) avisoDAO.create()
				.set("NUAVISO", null)
				.set("CODUSUREMETENTE",null)
				.set("CODUSU", codUsu) // DEFINIR USUÁRIO QUE IRÁ RECEBER A NOTIFICAÇÃO
				.set("TITULO", titulo)
				.set("DESCRICAO", obs)
				.set("DHCRIACAO", TimeUtils.getNow())
				.set("IDENTIFICADOR", "PERSONALIZADO")
				.set("IMPORTANCIA", BigDecimal.valueOf(2))
				.set("SOLUCAO", null)
				.set("TIPO", "P")
				.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MGEModelException.throwMe(e);
			System.out.println("Erro ao Executar Evento notifUsu" + e.getCause() + e.getMessage());
		}
	}
	public  void notifUsuUnico( String obs, String titulo)
			throws MGEModelException {
		JapeWrapper avisoDAO = JapeFactory.dao("AvisoSistema");
		try {
			
				DynamicVO avisoVO = (DynamicVO) avisoDAO.create()
						.set("NUAVISO", null)
						.set("CODUSUREMETENTE", BigDecimal.valueOf(0))
						.set("CODUSU", BigDecimal.valueOf(0)) // Notificar usuário individualmente																		
						.set("TITULO", titulo)
						.set("DESCRICAO", obs)
						.set("DHCRIACAO", TimeUtils.getNow())
						.set("IDENTIFICADOR", "PERSONALIZADO")
						.set("IMPORTANCIA", BigDecimal.valueOf(3))
						.set("SOLUCAO", null)  
						.set("TIPO", "P")
						.save();

			
		} catch (Exception e) {
			e.printStackTrace();
			MGEModelException.throwMe(e);
			System.out.println("Erro ao Executar Evento notifUsu" + e.getCause() + e.getMessage());
		}
	}
}