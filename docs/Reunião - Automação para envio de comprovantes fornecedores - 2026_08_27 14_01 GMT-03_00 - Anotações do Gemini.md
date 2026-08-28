ago. 27, 2026

## **Reunião \- Automação para envio de comprovantes fornecedores**

convidado [Francisco Natanael Lopes Vasconcelos](mailto:natanael.lopes@argofruta.com) [Aparecida Siqueira](mailto:aparecida@argofruta.com) [Thaiana Viana Rodrigues](mailto:thaiana.viana@argofruta.com)

Anexos [Reunião - Automação para envio de comprovantes fornecedores](https://calendar.google.com/calendar/event?eid=NmVjZXRuMHMwdDhlMGswMHVsYnFuOHFkdjYgdGhhaWFuYS52aWFuYUBhcmdvZnJ1dGEuY29t)

Registros da reunião [Transcrição](https://docs.google.com/document/d/1hHzcPztDS08Hp7TJcRL0Pb796kwQbzo2IWSwu7eRg7A/edit?usp=drive_web&tab=t.vztg2d3rk8fi) *(Algumas gravações estão indisponíveis)*

### **Resumo**

Reunião de alinhamento técnico sobre automação de e-mails e ajustes de filtros em transações financeiras.

**Automação e e-mails múltiplos**  
Implementada funcionalidade para envio automático de comprovantes para múltiplos endereços eletrônicos. Validações estritas de formatação de caracteres são necessárias para garantir o reconhecimento pelo sistema.

**Ajuste de campo da fatura**  
Criado novo campo de texto para o número da fatura exclusivo para CMA e F-Trade. Decidido que este campo não será de preenchimento obrigatório no sistema.

**Restrição de operações financeiras**  
Ajustada a regra do disparador de comprovantes para filtrar exclusivamente a transação TOP 1500\. A restrição elimina envios indevidos em operações de compensação e devolução.

### **Decisões**

## Alinhada

* **Padrão para e-mails múltiplos** O cadastro de e-mails múltiplos deve utilizar ponto e vírgula sem espaço para separar os endereços de e-mail no sistema.

* **Campo Invoice CMA criado** Será criado um novo campo de texto não obrigatório no cabeçalho das notas para o preenchimento do número da invoice da CMA.

* **Filtro de envio por TOP** O envio automático de comprovantes de pagamento será restringido apenas à TOP de baixa 1500, excluindo compensações e outros tipos de baixa.

**Atualizamos a seção "Decisões"** com base no seu feedback.

Dê sua opinião: [Sim](https://google.qualtrics.com/jfe/form/SV_5bXzKQfylMIhSXc?isHelpful=True&entryPoint=decisions&confid=pSB9iXf1yADLNi2YyMibDxIVOBEQAjIGCIoCIAAYBQg&isGoogler=False) ou [Não é útil](https://google.qualtrics.com/jfe/form/SV_5bXzKQfylMIhSXc?isHelpful=False&entryPoint=decisions&confid=pSB9iXf1yADLNi2YyMibDxIVOBEQAjIGCIoCIAAYBQg&isGoogler=False)

### **Próximas etapas**

- [ ] \[Francisco Natanael Lopes Vasconcelos\] Criar Campo Invoice: Criar novo campo no cabeçalho da Central de Vendas para registrar o número da Invoice da CMA, permitindo o uso de caracteres alfanuméricos.

- [ ] \[Aparecida Siqueira\] Atualizar Layouts: Configurar os layouts de pedidos e notas para incluir o novo campo de Invoice, garantindo sua visibilidade para preenchimento pelos usuários.

- [ ] \[Thaiana Viana Rodrigues\] Revisar Lista Fornecedores: Revisar com Jaqueline a lista de fornecedores que necessitam de múltiplos e-mails e atualizar o cadastro dos contatos no campo de e-mails múltiplos.

- [ ] \[Francisco Natanael Lopes Vasconcelos\] Ajustar Número Documento: Ajustar a rotina de envio de comprovantes para exibir o número da nota fiscal em vez do número único, facilitando a identificação pelos fornecedores.

- [ ] \[Francisco Natanael Lopes Vasconcelos\] Ajustar regra Top: Implementar regra para disparar comprovantes de pagamento apenas para registros com o tipo de operação 1500\.

- [ ] \[Aparecida Siqueira\] Formalizar ajuste: Enviar e-mail formalizando o ajuste realizado no campo de número da invoice da CMA para manter a equipe de logística ciente.

### **Detalhes**

* **Configuração de Múltiplos E-mails no Cadastro de Parceiros**: Francisco Natanael Lopes Vasconcelos apresentou a funcionalidade de e-mails múltiplos desenvolvida no cadastro de parceiros para permitir o envio automático de comprovantes para mais de um endereço eletrônico. A automação valida se a flag de ativação e a de notificação enviada estão habilitadas para realizar o disparo ([00:03:42](?tab=t.vztg2d3rk8fi#heading=h.ct2vx25f9hsf)) ([00:06:33](?tab=t.vztg2d3rk8fi#heading=h.fiyy3ixb7eip)). Quando há múltiplos cadastros, o sistema envia cópias para os endereços adicionais desde que estejam formatados estritamente com ponto e vírgula e sem espaços (por exemplo, \`email1@exemplo.com;email2@exemplo.com\`), pois caracteres ou espaçamentos incorretos impedem o reconhecimento pela rotina ([00:08:40](?tab=t.vztg2d3rk8fi#heading=h.7c37n91sasd5)) ([00:10:40](?tab=t.vztg2d3rk8fi#heading=h.swzzlsfjsb66)). Francisco Natanael Lopes Vasconcelos compartilhou uma planilha com os registros atuais para que Thaiana Viana Rodrigues revise os dados e corrija os formatos inadequados ([00:09:25](?tab=t.vztg2d3rk8fi#heading=h.g0dqknhso49x)) ([00:13:56](?tab=t.vztg2d3rk8fi#heading=h.dmt8jk8bvym0)).

* **Identificação da Fatura da CMA no Envio de Comprovantes**: Thaiana Viana Rodrigues relatou que o fornecedor CMA exige a identificação do número da fatura (invoice) nos pagamentos realizados, visto que essa empresa não opera com boletos bancários ([00:14:37](?tab=t.vztg2d3rk8fi#heading=h.ucqsvjurarc2)) ([00:18:37](?tab=t.vztg2d3rk8fi#heading=h.motg555ajc9h)). Como o campo padrão aceita apenas caracteres numéricos, a informação vinha sendo registrada no campo de observação ([00:14:37](?tab=t.vztg2d3rk8fi#heading=h.ucqsvjurarc2)) ([00:28:00](?tab=t.vztg2d3rk8fi#heading=h.uxj07gjd3dcq)). Para solucionar essa demanda, Francisco Natanael Lopes Vasconcelos criou um novo campo de texto no cabeçalho da nota destinado especificamente ao código da fatura da CMA ([00:17:05](?tab=t.vztg2d3rk8fi#heading=h.d7bfw5f3zjtr)) ([00:26:07](?tab=t.vztg2d3rk8fi#heading=h.cywr8awxwlun)). Christian Marques e a participante identificada como Documentation confirmaram que a alteração atende ao processo, esclarecendo que a exigência aplica-se exclusivamente à CMA e à F-Trade, enquanto os demais fornecedores continuam utilizando o número da nota fiscal padrão ([00:20:38](?tab=t.vztg2d3rk8fi#heading=h.9wytn427xgr4)) ([00:26:07](?tab=t.vztg2d3rk8fi#heading=h.cywr8awxwlun)).

* **Atualização de Layout para o Campo de Fatura**: Aparecida Siqueira assumiu a responsabilidade de atualizar os layouts de pedidos e notas para disponibilizar o novo campo de texto criado por Francisco Natanael Lopes Vasconcelos ([00:21:21](?tab=t.vztg2d3rk8fi#heading=h.dn6pqesmkrhx)) ([00:28:36](?tab=t.vztg2d3rk8fi#heading=h.gubrj0d6vdct)). Ficou decidido que o campo não será configurado como obrigatório no sistema, pois a exigência é restrita a operações específicas da CMA, evitando impactos ou cadastros desnecessários em transações de outros fornecedores ([00:28:36](?tab=t.vztg2d3rk8fi#heading=h.gubrj0d6vdct)) ([00:30:45](?tab=t.vztg2d3rk8fi#heading=h.8gxic4rap491)). Aparecida Siqueira encarregou-se de formalizar a orientação para a equipe de logística assim que a alteração no layout estivesse concluída ([00:47:59](?tab=t.vztg2d3rk8fi#heading=h.dex58k3cec32)).

* **Validação da Numeração de Notas nos Comprovantes**: Thaiana Viana Rodrigues questionou se os comprovantes disparados continham o número correto da nota em vez do número único gerado pelo sistema ([00:35:07](?tab=t.vztg2d3rk8fi#heading=h.d00d5r75iz2j)). Francisco Natanael Lopes Vasconcelos confirmou que a rotina envia o número do documento financeiro e os dados da baixa, garantindo a correta identificação para os parceiros ([00:36:17](?tab=t.vztg2d3rk8fi#heading=h.2icva9b32tdz)).

* **Restrição do Disparador de Comprovantes por Tipo de Operação (TOP)**: Thaiana Viana Rodrigues identificou que o sistema estava disparando comprovantes incorretamente com base em transações de compensação de pagamento e devolução (como as operações TOP 1511, 1407 e 1530), em vez de restringir-se aos pagamentos efetivos ([00:38:47](?tab=t.vztg2d3rk8fi#heading=h.mxr3f56eykj3)) ([00:42:09](?tab=t.vztg2d3rk8fi#heading=h.8fmlh0q158xp)). Francisco Natanael Lopes Vasconcelos concordou em ajustar a regra da automação para filtrar o envio exclusivamente utilizando a transação de baixa TOP 1500, que representa pagamentos normais via banco. Com essa filtragem restrita à TOP 1500, o sistema também deixará de enviar indevidamente títulos de receita, resolvendo os problemas pendentes desde maio ([00:43:05](?tab=t.vztg2d3rk8fi#heading=h.vq0gbehf1r3x)) ([00:46:45](?tab=t.vztg2d3rk8fi#heading=h.ffby00qrzl7t)).

*Revise as anotações do Gemini para checar se estão corretas. [Confira dicas e saiba como o Gemini faz anotações](https://support.google.com/meet/answer/14754931)*

*Como está a qualidade de **destas observações?** [Responda a uma breve pesquisa](https://google.qualtrics.com/jfe/form/SV_5bXzKQfylMIhSXc?confid=pSB9iXf1yADLNi2YyMibDxIVOBEQAjIGCIoCIAAYBQg&detailLevel=standard&hasImages=False&entryPoint=footerMain&isGoogler=False) para nos dar seu feedback, incluindo o quanto as observações foram úteis para o que você precisa.*