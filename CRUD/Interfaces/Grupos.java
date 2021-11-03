package Interfaces;
import java.util.Scanner;
import DataBase.*;
import Props.Grupo;
import Utils.Utils;
import java.util.Date;

/*	Interface de gerenciamento de grupos
 */
public class Grupos {
    private CRUD<Grupo> banco;
    private ArvoreBMais_Int_Int indice;
    private final String diretorio = "dados";
	private int logged;
	private String dataFormato;

	//Construtor do grupo, inicializa todos os bancos de dados e cria o formato padrão da data
    public Grupos(String nomeArquivo){
        try{
            banco   = new CRUD<>(nomeArquivo + " Grupos", Grupo.class.getConstructor());
			indice = new ArvoreBMais_Int_Int(10,diretorio+ "/grupos"+nomeArquivo+".idx");
            dataFormato = "dd/MM/yyyy HH:mm";
        }catch(Exception e){
            System.out.println("Erro ao criar interface de Grupos.");
        }//end of catch
    }//end of Grupos

	//Método para mudar o id do usuario logado no sistema
    public void changeLogged(int logged){
		this.logged = logged;
	}//end of changeLogged

	//Método que seleciona qual dos métodos do menu de grupos ira ser chamado
	public void gerenciamento(Scanner entrada){
		int opcao;
		boolean end = false;
		while(!end){
			Utils.limparTela();
			System.out.println("Amigo Oculto 1.0\n================================================");
			System.out.println("INÍCIO > GRUPOS > MENU GRUPOS > GERENCIAMENTO DE GRUPOS: \n");
			System.out.println("1) Listar");
			System.out.println("2) Incluir");
			System.out.println("3) Alterar");
			System.out.println("4) Desativar\n");
			System.out.println("0) Retornar ao menu de Grupos\n");
			System.out.print("Digite sua opção: ");
			opcao = entrada.nextInt();
			entrada.nextLine();
			switch(opcao){
				case 0:
					end = true;
					break;
				case 1:
					listar(entrada);
					break;
				case 2:
					incluir(entrada);
					break;
				case 3:
					alterar(entrada);
					break;
				case 4:
					desativar(entrada);
					break;
				default:
					System.out.println("Opção inválida");
					Utils.travarTela(entrada);
			}//end of switch
		}//end of while
	}//end of gerenciamentoDeGrupos

	//Método de listagem de grupos:
	public void listar(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.println("MEUS GRUPOS:\n");
			//Primeiro pegar todos os ids de grupos que estão logados no sistema:
			int[] id = indice.read(logged);
			//Mostrar todos os grupos que existem no sistema:
			for(int i = 0; i < id.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(id[i]));
			}//end of for
			Utils.travarTela(entrada);
		} catch(Exception e){
			System.out.print("Erro ao listar grupo: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of listarGrupos

	//Incluir um novo grupo no banco de dados:
	public void incluir(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar todas as informações necessárias para a criação do grupo:
			String nome,dataEncontro,dataSorteio,local,observacao;
			float valor;
			System.out.println("INCLUIR GRUPO:\n");
			System.out.print("Digite o nome do novo grupos: ");
			nome = entrada.nextLine();
			if(nome.trim().isEmpty())
				throw new Exception("Nome inválido");
			
			dataSorteio = Utils.getDate(entrada,"Digite a data e hora do sorteio no formato(dd/MM/aaaa HH:mm): ");
			Date sorteio = Utils.readDate(dataSorteio, dataFormato);
			if(!Utils.checkDate(sorteio))
				throw new Exception("Data de sorteio inválida.");
			
			valor = Utils.getFloat("Digite o valor médio dos presentes: R$ ", entrada);
			if(valor < 0.f){
				valor = 0.f;
			}//end of if
			
			dataEncontro = Utils.getDate(entrada, "Digite a data e hora do encontro no formato(dd/MM/aaaa HH:mm): ");
			Date encontro = Utils.readDate(dataEncontro,dataFormato);
			if(encontro.compareTo(sorteio) <= 0)
				throw new Exception("Data de encontro inválido.");

			System.out.print("Digite o local do encontro: ");
			local = entrada.nextLine();
			if(local.trim().isEmpty())
				throw new Exception("Local inválido.");

			System.out.print("Digite as observações adicionais (opcional): ");
			observacao = entrada.nextLine();

			//Questionar se deseja adicionar o grupo na base de dados:
			if(Utils.askQuestion("Tem certeza de que quer criar este grupo? ", entrada)){
				//Criar um grupo com as informações obtidas do usuario:
				Grupo inserir = new Grupo(-1,logged,nome,sorteio.getTime(),valor,
										  encontro.getTime(),local,observacao);
				//Criar as informações dentro do banco e no indices
				int id = banco.create(inserir);
				indice.create(logged,id);
				System.out.println("Grupo criado com sucesso.");
			} else {
				System.out.println("Operação cancelada com sucesso.");
			}//end of if
			Utils.travarTela(entrada);
		} catch(Exception e){
			System.out.print("Erro ao incluir grupo: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of incluirGrupos

	//Método que altera os dados do grupo
	public void alterar(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.println("ALTERAR GRUPO:\n");
			//Pegar todos os ids do grupo que se relacionam com o usuario
			int[] id = indice.read(logged);
			for(int i = 0; i < id.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(id[i]));
			}//end of for
			System.out.println("Digite o numero do grupo que deseja alterar: ");
			//Adquirir qual o grupo que o usuário deseja alterar:
			int grupo = entrada.nextInt();
			entrada.nextLine();
			grupo -= 1;
			if(grupo < 0 || grupo > id.length -1)
				throw new Exception("Grupo inválido.");
			
			//Adquirir todos os dados do grupo:
			Grupo alterar = banco.read(id[grupo]);
			String nome, local, observacao;
			Date encontro, sorteio;
			float valor;
			boolean end = false;
			encontro = new Date();
			sorteio = new Date();

			System.out.println("O campo que não desejar alterar basta deixá-lo em branco.");

			System.out.print("Digite o novo nome do grupo: ");
			nome = entrada.nextLine();
			if(nome.trim().isEmpty())
				nome = alterar.getNome();

			String data = Utils.getDate(entrada,"Digite a data e hora do sorteio no formato(dd/MM/aaaa HH:mm): ");
			if(!data.trim().isEmpty()){
				sorteio = Utils.readDate(data,dataFormato);
				if(!Utils.checkDate(sorteio))
					throw new Exception("Data do sorteio inválida.");
			} else {
				sorteio = new Date(alterar.getMomentoSorteado());
			}//end of if


			if(Utils.askQuestion("Deseja alterar o valor médio do grupo? ", entrada)){
				System.out.print("Digite o novo valor médio do grupo: R$ ");
				valor = entrada.nextFloat();
				if(valor < 0.f)
					throw new Exception("Valor médio inválido.");
			} else {
				valor = alterar.getValor();
			}//end of if

			while(!end){
				data = Utils.getDate(entrada,"Digite a data e hora do encontro no formato(dd/MM/aaaa HH:mm): ");
				if(!data.trim().isEmpty()){
					encontro = Utils.readDate(data, dataFormato);
					if(Utils.checkDate(sorteio))
						end = true;
				} else {
					encontro = new Date(alterar.getMomentoEncontro());
					end = true;
				}//end of if
			}//end of while

			System.out.print("Digite o novo local do encontro do grupo: ");
			local = entrada.nextLine();
			if(local.trim().isEmpty())
				local = alterar.getLocal();


			System.out.print("Digite as novas observações do grupo: ");
			observacao = entrada.nextLine();
			if(observacao.trim().isEmpty()){
				observacao = alterar.getObservacoes();
			}//end of if

			//Perguntar o usuário se ele deseja realmente alterar o grupo
			if(Utils.askQuestion("Deseja realmente alterar este grupo? ",entrada)){
				//Criar um novo grupo com os dados obtidos
				alterar = new Grupo(id[grupo], logged, nome, sorteio.getTime(),
									valor, encontro.getTime(), local, observacao);
				//Alterar no banco de dados
				banco.update(alterar);
				System.out.println("Grupo alterado com sucesso.");
			} else {
				System.out.println("Operação cancelada com sucesso.");
			}//end of if

			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao excluir grupo: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of alterarGrupos

	//Método para desetivar todos os itens do dado:
	public void desativar(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.println("ALTERAR GRUPO:\n");
			//Pegar todos os ids do grupo
			int[] id = indice.read(logged);
			for(int i = 0; i < id.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(id[i]));
			}//end of for
			//Pegar qual o grupo que o usuario deseja alterar:
			System.out.println("Digite o numero do grupo que deseja alterar: ");
			int grupo = entrada.nextInt();
			entrada.nextLine();
			//Perguntar se o usuario deseja realmente desativar o grupo:
			if(Utils.askQuestion("Tem certeza que deseja desativar este grupo? ",entrada)){
				//Obter o grupo a ser desativado:
				Grupo desativar = banco.read(id[grupo-1]);
				if(!desativar.estaAtivo())
					throw new Exception("Grupo já foi desativado");
				//Mudar o grupo e coloca-lo como desativado:
				desativar.ativar(false);
				banco.update(desativar);
				System.out.println("Desativamento efetuado com sucesso");
			}else {
				System.out.println("Operação cancelada com sucesso. ");
			}//end of else
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao desativar grupo: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of try
	}//end of desativarGrupos

	//Método que atualiza o estado de sorteio de um grupo:
	public void sorteado(int id) throws Exception{
		Grupo g = banco.read(id);
		g.sortear(true);
		banco.update(g);
	}//end of sorteado

	//Método que retorna todos os grupos pertencentes ao usuário logado:
	public Grupo[] getGrupos() throws Exception{
		//Pegar o id de todos os grupos
		int[] id = indice.read(logged);
		//Criar um arranjo de grupos e inserir todos os grupos nele:
		Grupo[] g = new Grupo[id.length];
		for(int i = 0; i < id.length; i++ ){
			g[i] = banco.read(id[i]);
		}//end of for
		return g;
	}//end of getGrupos

	//Método que dado um token, busca na base de dados e o retorna como requisição.
	//Retorna nulo se o objeto não existir na base de dados.
	public Grupo getGrupo(int token){
		return banco.read(token);
	}//end of getGrupo
}//End of grupos interface