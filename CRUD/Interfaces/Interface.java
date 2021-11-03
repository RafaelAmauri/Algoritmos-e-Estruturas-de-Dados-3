package Interfaces;

import java.util.Scanner;
import Props.Grupo;
import Props.Convite;
import Props.Participante;
import Utils.Utils;

/*	Interface principal do crud
 */
public class Interface{
	private Sugestoes iSugestoes;
	private Grupos iGrupos;
	private Convites iConvites;
    private Usuarios iUsuarios;
	private Participantes iParticipantes;
	private Mensagens iMensagens;
	private String email;
	private String dataFormato;
	private int logged;

    public Interface(String nomeArquivo){
        try{
			iSugestoes = new Sugestoes(nomeArquivo);
			iGrupos = new Grupos(nomeArquivo);
			iConvites = new Convites(nomeArquivo);
            iUsuarios = new Usuarios(nomeArquivo);
			iParticipantes = new Participantes(nomeArquivo);
			iMensagens = new Mensagens(nomeArquivo);
			dataFormato = "dd/MM/yyyy HH:mm";
			email = "";
        }catch(Exception e){
            System.out.print("Erro ao criar interface: ");
            e.printStackTrace();
        }//end of catch
    }//end of Sistema constructor
	
	private void atualizarToken(int token,String e){
		email = e;
		logged = token;
		iGrupos.changeLogged(token);
		iSugestoes.changeLogged(token);
		iMensagens.changeToken(token);
	}//end of atualizar Token

	//Menu de login:
	public void menu(){
		try{
			boolean end = false;
			Scanner reader = new Scanner(System.in);
			int opcao;
			while(!end){
				Utils.limparTela();
				System.out.printf("Amigo Oculto 1.0\n=============================\n\nACESSO:\n\n");
				System.out.println("1)Acesso ao sistema");
				System.out.println("2)Novo usuario (primeiro acesso)");
				System.out.printf("\n\n0)Sair");
				System.out.printf("\n\nOpção: ");
				opcao = reader.nextInt();
				reader.nextLine();
				switch(opcao){
					case(1):
						int token = iUsuarios.acessar(reader);
						String email = iUsuarios.getEmail(token);
						if(token >= 0){
							atualizarToken(token,email);
							telaPrincipal(reader);
						}//end of valid user
						break;
					case(2):
						iUsuarios.criar(reader);
						break;
					case(0):
						end = true;
						break;
					default:
						System.out.println("Opção inválida, tente novamente.");
				}//end of switch
			}//end of while
			reader.close();
		}catch(Exception e){
			System.out.print("Erro no menu: ");
			e.printStackTrace();
		}//end of catch
	}//end of menu

	//Menu principal:
    private void telaPrincipal(Scanner entrada){
		boolean end = false;
		int opcao;
		int quantidade_convites;
		while(!end){
			Utils.limparTela();
			quantidade_convites = iConvites.quantConvites(email);
			System.out.printf("Amigo Oculto 1.0\n=============================\n\nINÍCIO:\n\n");
			System.out.println("1)Sugestão de presentes.");
			System.out.println("2)Grupos.");
			System.out.println("3)Novos convites: " + quantidade_convites);
			System.out.printf("\n0)Sair\n\n");
			System.out.print("Opção: ");
			opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 0:
					end = true;
					break;
				case 1:	
					iSugestoes.tela(entrada);
					break;
				case 2:
					telaDeGrupos(entrada);
					break;
				case 3:
					telaConvitesAlteracao(entrada);
					break;
				default:
					System.out.print("Entrada inválida, tente novamente: ");
					entrada.nextLine();
			}//end of menu choices
		}//end of main screen loop
		atualizarToken(-1, "");
	}//end of telaPrincipal

	//Menu de grupos:
	public void telaDeGrupos(Scanner entrada){
		int opcao;
		boolean end = false;
		while(!end){
			Utils.limparTela();
			System.out.println("Amigo Oculto 1.0\n================================================");
			System.out.println("INÍCIO > GRUPOS: \n");
			System.out.println("1) Criação e gerenciamento grupos.");
			System.out.println("2) Participação nos grupos.\n");
			System.out.println("0) Retornar ao início\n");
			System.out.print("Digite sua opção: ");
			opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 0:
					end = true;
					break;
				case 1:
					grupos(entrada);
					break;
				case 2:
					telaParticipantesGrupos(entrada);
					break;
				default:
					System.out.println("Opção inválida, tente novamente");
					Utils.travarTela(entrada);
			}//end of switch
		}//end of while
	}//end of telaGrupos

	//Menu de gerenciamento de grupos, convites, participantes e sorteio:
	private void grupos(Scanner entrada){
		int opcao;
		boolean end = false;
		while(!end){
			Utils.limparTela();
			System.out.println("Amigo Oculto 1.0\n================================================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS: \n");
			System.out.println("1) Grupos");
			System.out.println("2) Convites");
			System.out.println("3) Participantes");
			System.out.println("4) Sorteio\n");
			System.out.println("0) Retornar ao menu de Grupos\n");
			System.out.print("Digite sua opção: ");
			opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 0:
					end = true;
					break;
				case 1:
					iGrupos.gerenciamento(entrada);
					break;
				case 2:
					telaDeConvites(entrada);
					break;
				case 3:
					telaParticipantes(entrada);
					break;
				case 4:
					telaSorteio(entrada);
					break;
				default:
					System.out.println("Opção inválida.");
					Utils.travarTela(entrada);
			}//end of switch
		}//end of while
	}//end of grupos
	
	//Menu de convites:
	private void telaDeConvites(Scanner entrada){
		int opcao;
		boolean end = false;
		while(!end){
			Utils.limparTela();
			System.out.println("Amigo Oculto 1.0\n================================================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > GERENCIAMENTO DE CONVITES: \n");
			System.out.println("1) Listagem dos convites");
			System.out.println("2) Emissão dos convites");
			System.out.println("3) Cancelamento dos convites\n");
			System.out.println("0) Retornar ao menu anterior\n");
			System.out.print("Digite sua opção: ");
			opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 1:
					telaDeConvitesListagem(entrada);
					break;
				case 2:
					telaDeConvitesEmissao(entrada);
					break;
				case 3:
					telaDeConvitesCancelamento(entrada);
					break;
				case 0:
					end = true;
					break;
				default:
					System.out.println("Opção inválida, tente novamente.");
					Utils.travarTela(entrada);
			}//end of switch
		}//end of while
	}//end of telaDeConvites

	//Menu anterior ao de listagem de convites, um auxiliar:
	private void telaDeConvitesListagem(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todos os grupos:
			Grupo[] g = iGrupos.getGrupos();
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > GERENCIAMENTO DE CONVITES > LISTAGEM: \n");
			System.out.println("Meus grupos: \n ");
			//Listar todos os grupos:
			for(int i = 0; i < g.length; i++ ){
				System.out.println( (i + 1) + ". " + g[i]);
			}//end of for
			//Pegar qual o grupo desejado do usuario:
			System.out.print("Digite a opção desejada para a listagem de convites: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			//Verificar se a opção digitada é válida:
			if( opcao < 0 || opcao > g.length -1)
				throw new Exception("Opção inválida.");

			if( !g[opcao].estaAtivo())
				throw new Exception("Grupo não está ativo.");

			if( g[opcao].sorteado())
				throw new Exception("Grupo já foi sorteado.");
			
			//Chamar o método de convite listagem de convite:
			iConvites.listar(g[opcao].getID(), entrada, g[opcao].getNome());
		} catch ( Exception e ) {
			System.out.println("Erro ao listar convites.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of listagem

	//Método anterior ao método de emissão de convites:
	private void telaDeConvitesEmissao(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todos os grupos:
			Grupo[] g = iGrupos.getGrupos();
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > GERENCIAMENTO DE CONVITES > EMISSÃO: \n");
			System.out.println("Meus grupos: \n ");
			//Listar grupos:
			for(int i = 0; i < g.length; i++ ){
				System.out.println( (i + 1) + ". " + g[i]);
			}//end of for
			//Pegar qual o grupo desejado do usuario:
			System.out.print("Digite a opção desejada para a emissão de convites: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			//Verificar se a opção digitada é válida:
			if( opcao < 0 || opcao > g.length -1)
				throw new Exception("Opção inválida.");

			if( !g[opcao].estaAtivo())
				throw new Exception("Grupo não está ativo.");

			if( g[opcao].sorteado())
				throw new Exception("Grupo já foi sorteado.");
			
			//Chamar o método de convite emissão de convite:
			iConvites.emitir(g[opcao].getID(), entrada, g[opcao].getNome());
		} catch ( Exception e ) {
			System.out.println("Erro ao emissão convites.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of emissao

	//Tela de gerenciamento de cancelamento de convites
	private void telaDeConvitesCancelamento(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todos os grupos:
			Grupo[] g = iGrupos.getGrupos();
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > GERENCIAMENTO DE CONVITES > CANCELAMENTO: \n");
			System.out.println("Meus grupos: \n ");
			//Listar grupos:
			for(int i = 0; i < g.length; i++ ){
				System.out.println( (i + 1) + ". " + g[i]);
			}//end of for
			//Pegar qual o grupo desejado do usuario:
			System.out.print("Digite a opção desejada para o cancelamento de convites: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			if( opcao < 0 || opcao > g.length -1)
				throw new Exception("Opção inválida.");
			
			if( !g[opcao].estaAtivo())
				throw new Exception("Grupo não está ativo.");

			if( g[opcao].sorteado())
				throw new Exception("Grupo já foi sorteado.");
			
			//Chamar o método de convite cancelar de convite:
			iConvites.cancelar(g[opcao].getID(), entrada, g[opcao].getNome());
		} catch ( Exception e ) {
			System.out.println("Erro ao cancelamento convites.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of cancelamento

	//Tela principal para aceitação ou recusa de convites:
	private void telaConvitesAlteracao(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.println("VOCÊ FOI CONVIDADO PARA PARTICIPAR DOS GRUPOS ABAIXO.");
			System.out.println("ESCOLHA QUAL CONVITE DESEJA ACEITAR OU RECUSAR:\n");
			//Pegar todos os convites relacionados ao email logado:
			Convite[] c = iConvites.getConvites(email);
			Grupo g = null;
			String nome = "";
			String data = "";
			//Listar todos os convites e suas informações necessárias:
			for(int i = 0; i < c.length; i++){
				g = iGrupos.getGrupo(c[i].getIDGrupo());
				nome = iUsuarios.getNome(g.getIDUsuario());
				data = Utils.dateToString(c[i].getMomentoConvite(), dataFormato);
				System.out.println( (i + 1) + ". " + g.getNome());
				System.out.println( "   Convidado em " + data );
				System.out.println( "   por " + nome);
			}//end of for
			//Pegar a opção do usuário
			System.out.print("Convite: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			//Verificar a opção do usuario
			if( opcao < 0 || opcao > c.length -1)
				throw new Exception("Opcao inválida");
			
			//Chamar o método de alteração dos convites:
			int id = c[opcao].getID();
			iConvites.telaSecundaria(id, logged, entrada, iParticipantes);
		} catch ( Exception e ) {
			System.out.println("Erro ao alterar convite.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of convitesAlteracao

	//Tela de partiicipantes vista por administradores, simples menu não é necessário muitas explicações.
	public void telaParticipantes(Scanner entrada){
		boolean end = false;
		while(!end){
			Utils.limparTela();
			System.out.println("Amigo oculto V1.0");
			System.out.println("========================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > PARTICIPANTES: \n");
			System.out.println("1) Listar.");
			System.out.println("2) Remover.\n");
			System.out.println("0) Retornar.\n");
			System.out.print("Digite a opção desejada: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 1:
					listarParticipantes(entrada);
					break;
				case 2:
					removerParticipantes(entrada);
					break;
				case 0:
					end = true;
					break;
				default:
					System.out.println("Opção inválida, tente novamente.");
			}//end of switch
		}//end of while
	}//end of listar

	//Método de listagem de participantes
	public void listarParticipantes(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todos os grupos que pertencem ao usuario:
			Grupo[] g = iGrupos.getGrupos();
			int counting = 0;
			//Verificar os que estão ativos
			for(int i = 0 ; i < g.length; i++){
				if(!g[i].estaAtivo()){
					g[i] = null;
					counting += 1;
				}//end of if
			}//end of for
			if(counting != 0){
				//Adicionar todos os grupos ativos dentro do arranjo:
				Grupo[] l = new Grupo[g.length-counting];
				counting = 0;
				for(int i = 0; i < g.length; i++){
					if(g[i]!=null){
						l[counting] = g[i];
						counting += 1;
					}//end of if
				}//end of for
				g = l;
				l = null;
			}//end of if
			//Obter qual o grupo que o usuario deseja visualizar os participantes:
			System.out.println("Amigo oculto V1.0");
			System.out.println("========================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > PARTICIPANTES > LISTAGEM: \n");
			for(int i = 0; i<g.length; i++){
				System.out.println((i+1) + ". " + g[i]);
			}//end of for
			System.out.print("Digite o grupo que deseja listar os participantes: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			if(opcao < 0 || opcao >= g.length)
				throw new Exception("Opção inválida.");
			
			int id = g[opcao].getID();
			//Obter todos os participantes do grupo desejado:
			Participante[] p = iParticipantes.listar(id);
			//Verificar se existe algum participante:
			if(p == null){
				System.out.println("Desculpe, infelizmente este grupo não tem nenhum participante.");
			} else {
				//Mostar o nome de todos os participantes:
				for(int i = 0; i < p.length; i++){
					int iU = p[i].getIDUsuario();
					String u = iUsuarios.getNome(iU);
					System.out.println(i+1 + ". " + u);
				}//end of for
			}//end of if
			Utils.travarTela(entrada);
		} catch(Exception e){
			System.out.println("Erro ao listar participantes.");
			e.printStackTrace();
		}//end of try
	}//end of listarParticipantes

	//Tela principal para a remoção de um participante:
	public void removerParticipantes(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todos os grupos pertencentes ao usuario:
			Grupo[] g = iGrupos.getGrupos();
			int counting = 0;
			for(int i = 0 ; i < g.length; i++){
				//Verificar os que estão ativos
				if(!g[i].estaAtivo()){
					g[i] = null;
					counting += 1;
				}//end of if
			}//end of for
			if(counting != 0){
				//Adicionar os ativos em um novo vetor:
				Grupo[] l = new Grupo[g.length-counting];
				counting = 0;
				for(int i = 0; i < g.length; i++){
					if(g[i]!=null){
						l[counting] = g[i];
						counting += 1;
					}//end of if
				}//end of for
				g = l;
				l = null;
			}//end of if
			System.out.println("Amigo oculto V1.0");
			System.out.println("========================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > PARTICIPANTES > REMOÇÃO: \n");
			for(int i = 0; i<g.length; i++){
				System.out.println((i+1) + ". " + g[i]);
			}//end of for
			//Obter o grupo que o usuario desja administar:
			System.out.print("Digite o grupo que deseja remover os participantes: ");
			int opcao = entrada.nextInt();
			entrada.nextLine();
			opcao -= 1;
			if(opcao < 0 || opcao >= g.length)
				throw new Exception("Opção inválida.");
			
			int id = g[opcao].getID();
			//Obter todos os participantes do grupo:
			Participante[] p = iParticipantes.listar(id);
			//Verificar a existência de algum participante:
			if(p == null){
				System.out.println("Desculpe, infelizmente este grupo não tem nenhum participante.");
			} else {
				//Mostrar todos os nomes de participantes do grupo:
				for(int i = 0; i < p.length; i++){
					int iU = p[i].getIDUsuario();
					String u = iUsuarios.getNome(iU);
					System.out.println(i+1 + ". " + u);
				}//end of for
				//Obter qual o participante que o usuario deseja remover:
				System.out.print("Digite o participante que deseja remover: ");
				int participante = entrada.nextInt();
				entrada.nextLine();
				participante -= 1;
				if( participante < 0 || participante >= p.length )
					throw new Exception("Participante inválido.");

				if(Utils.askQuestion("Deseja realmente remover este participante?", entrada)){
					//Remover o participante do grupo:
					iParticipantes.remover(p[participante].getID());
					System.out.println("Remoção efetuada com êxito.");
				} else {
					System.out.println("Operação cancelada.");
				}//end of if
			}//end of if
			Utils.travarTela(entrada);
		} catch(Exception e){
			System.out.println("Erro ao remover participantes.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of removerParticipantes

	//Tela de participante vista pelos participantes:
	public void telaParticipantesGrupos(Scanner entrada){
		try{
			//Pegar todos os grupos que o participante participa:
			int[] ids = iParticipantes.getGrupos(logged);
			//Verificar se existe algum grupo:
			if( ids.length < 1){
				System.out.println("INFELIZMENTE VOCÊ NÃO PARTICIPA DE NENHUM GRUPO NO MOMENTO.");
				Utils.travarTela(entrada);
			} else {
				//Mostrar todos os grupos:
				System.out.println("DIGITE O GRUPO QUE DESEJA VISUALIZAR.\n");
				for(int i = 0; i < ids.length; i++){
					System.out.println(i+1 + ". " + iGrupos.getGrupo(ids[i]));
				}//end of for
				//Obter o grupo a ser visualizado:
				System.out.print("\nGRUPO: ");
				int opcao = entrada.nextInt();
				opcao -= 1;
				entrada.nextLine();
				if( opcao < 0 || opcao >= ids.length )
					throw new Exception("Opção Inválida.");
				
				//Obter todas as informações do grupo:
				Grupo g = iGrupos.getGrupo(ids[opcao]);
				String nome = g.getNome();
				String local = g.getLocal();
				String sort = "";
				String observacoes = g.getObservacoes();
				long sorteio = g.getMomentoSorteado();
				long encontro = g.getMomentoEncontro();
				float valor = g.getValor();
				if(g.sorteado()){
					sort = "O sorteio ocorreu dia ";
				} else {
					sort = "O sorteio ocorrerá dia ";
				}//end of if
				boolean end = false;
				String sSorteio = Utils.dateToString(sorteio, dataFormato);
				String sEncontro = Utils.dateToString(encontro, dataFormato);
				//Entrar em um laço de repetção até o usuario cansar de usar o menu:
				while(!end){
					Utils.limparTela();
					//Obter qual operação deve ser realizada:
					System.out.println("Amigo oculto V1.0");
					System.out.println("========================");
					System.out.println("INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPOS: \n");
					System.out.println(nome.toUpperCase());
					System.out.println(sort + sSorteio + ".");
					System.out.println("Os presentes devem ter o valor aproximado de R$ " + (valor) + ".");
					System.out.println("O encontro ocorrerá dia " + sEncontro + ".");
					System.out.println("em " + local + ".\n");
					System.out.println("OBSERVAÇÕES:");
					System.out.println(observacoes + ".\n");
					System.out.println("1) Visualizar participantes.");
					System.out.println("2) Visualizar amigo sorteado.");
					System.out.println("3) Ler/Eviar mensagens do grupo.\n");
					System.out.println("0) Retornar.\n");
					System.out.print("Opção: ");
					int operacao = entrada.nextInt();
					entrada.nextLine();
					switch(operacao){
						case 0:
							end = true;
							break;
						case 1:
							visualizarParticipantes(ids[opcao], entrada);
							break;
						case 2:
							visualizarAmigo(ids[opcao], entrada);
							break;
						case 3:
							enviarMensagem(ids[opcao], nome, entrada);
							break;
						default:
							System.out.println("Opção inválida, tente novamente.");
							Utils.travarTela(entrada);
					}//end of 
				}//end of while
			}//end of if
		} catch(Exception e) {
			System.out.println("Erro na tela de participantes do grupo.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of 
	}//end of telaParticipantesGrupos

	//Visualização de participates:
	public void visualizarParticipantes(int idGrupo, Scanner entrada){
		//Obter todos os participante do grupo
		Participante[] p = iParticipantes.listar(idGrupo);
		//Verificar a existência de algum participante:
		if(p == null){
			System.out.println("Desculpe, infelizmente este grupo não tem nenhum participante.");
		} else {
			//Mostrar todos os participantes na tela:
			for(int i = 0; i < p.length; i++){
				int iU = p[i].getIDUsuario();
				String u = iUsuarios.getNome(iU);
				System.out.println(i+1 + ". " + u);
			}//end of for
		}//end of if
		Utils.travarTela(entrada);
	}//end of visualizarParticipantes

	//Visualizar o amigo sorteado do participante:
	public void visualizarAmigo(int idGrupo,Scanner entrada){
		try{
			//Obter o participante logado:
			Participante p = iParticipantes.getParticipante(logged, idGrupo);
			if(p.getIDAmigo() == -1)
				throw new Exception("Grupo não efetuou sorteio ainda.");

			//Obter o nome do amigo do usuario:
			String amigo = iUsuarios.getNome(p.getIDAmigo());
			String usuario = iUsuarios.getNome(logged);
			System.out.println("Amigo de " + usuario + ":\n");
			System.out.println("NOME: " + amigo);
			//Mostrar suas sugestões na tela:
			System.out.println("SUGESTÕES: ");
			iSugestoes.mostrar(p.getIDAmigo());
			Utils.travarTela(entrada);
		} catch(Exception e) {
			System.out.println("Erro ao visualizar amigo.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of visualizarAmigo

	//Enviar ou ler uma nova mensagem do grupo:
	public void enviarMensagem(int idGrupo, String nomeGrupo,Scanner entrada){
		try{
			//Mostrar interface de mensagens:
			iMensagens.tela(idGrupo, nomeGrupo, entrada, iUsuarios);
		} catch(Exception e) {
			System.out.println("Erro ao enviar mensagens.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of enviarMensagem

	//Tela para efetuação de um novo sorteio:
	public void telaSorteio(Scanner entrada){
		try{
			//Pegar todos os grupos pertencentes ao usuario:
			Grupo[] ids = iGrupos.getGrupos();
			//Verificar se existe algum grupo do usuario:
			if( ids.length <= 0){
				System.out.println("Usuario não possui nenhum grupo.");
				Utils.travarTela(entrada);
			} else {
				//Verificar se o grupo do usuario é valido
				int counting = 0;
				for(int i = 0; i < ids.length; i++){
					boolean valido = Utils.checkDate(ids[i].getMomentoSorteado());
					if(!ids[i].estaAtivo() || ids[i].sorteado() || valido){
						counting += 1;
						ids[i] = null;
					}//end of if
				}//end of for

				//Verificar se o usuario possui algum grupo válido:
				int valido = ids.length - counting;
				if(  valido == 0 ){
					System.out.println("Usuario não possui nenhum grupo válido");
					Utils.travarTela(entrada);
				} else {
					//Obter todos os grupos válidos:
					Grupo[] g = new Grupo[valido];
					counting = 0;
					for(int i = 0; i < ids.length; i++){
						if(ids[i] != null){
							g[counting] = ids[i];
							counting += 1;
						}//end of if
					}//end of for
					Utils.limparTela();
					//Obter qual grupo o usuario deseja efetuar o sorteio
					System.out.println("Amigo Oculto 1.0\n================================================");
					System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > SORTEIO: \n");
					System.out.println("DIGITE O GRUPO QUE DESEJA EFETUAR O SORTEIO: ");
					for(int i = 0; i < g.length; i++){
						System.out.println(i+1 + ". " + g[i]);
					}//end of for
					System.out.print("Opção: ");
					int opcao = entrada.nextInt();
					entrada.nextLine();
					opcao-=1;
					if(opcao < 0 || opcao >= g.length)
						throw new Exception("Opção inválida.");

					//Embaralhar e sortear todos os participantes:
					iParticipantes.embaralhar(g[opcao].getID());
					//Atualizar o estado do grupo:
					iGrupos.sorteado(g[opcao].getID());
					System.out.println("Operação efetuada com sucesso.");
					Utils.travarTela(entrada);
				}//end of if
			}//end of if
		} catch(Exception e) {
			System.out.println("Erro ao sortear grupo.");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of telaSorteio
}//end of Interface