package Interfaces;
import java.util.Scanner;
import Props.Sugestao;
import DataBase.*;
import Utils.Utils;

/* Interface de sugestões.
 */
public class Sugestoes {
	private CRUD<Sugestao> banco;
	private ArvoreBMais_Int_Int indice;
	private final String diretorio = "dados";
	private int logged;

	public Sugestoes(String nomeArquivo){
		try{
			banco = new CRUD<>(nomeArquivo + " Sugestao", Sugestao.class.getConstructor());
			indice = new ArvoreBMais_Int_Int(10,diretorio+ "/sugestões"+nomeArquivo+".idx");
		} catch ( Exception e ) {
			System.out.println("Erro ao criar interface de Sugestões");
			e.printStackTrace();
		}//end of catch
	}//end of Sugestoes

	public void changeLogged(int logged){
		this.logged = logged;
	}//end of changeLogged

	//Tela principal das execuções de determinadas ações da entidade de sugestão
   	public void tela(Scanner entrada){
		int opcao;
		boolean end = false;
		//Loop do menu:
		while(!end){
			Utils.limparTela();
			System.out.printf("Amigo Oculto 1.0\n=============================\n\nINÍCIO > SUGESTÕES:\n\n");
			System.out.println("1)Listar.");
			System.out.println("2)Incluir.");
			System.out.println("3)Alterar.");
			System.out.println("4)Excluir.");
			System.out.printf("\n\n0)Retornar ao menu anterior: ");
			System.out.printf("\n\nOpção: ");
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
					excluir(entrada);
					break;
				default:
					System.out.print("Opção inválida, tente novamente: ");
					entrada.nextLine();
			}//end of sugestion screen selector
		}//end of sugestion screen loop
	}//end of telaSugestao

	//Método de inclusão de uma nova sugestão:
	private void incluir(Scanner entrada){
		try{
			Utils.limparTela();
			//Pegar os dados da sugestão:
			String nome,loja,observacoes;
			float valor;
			System.out.printf("INCLUIR NOVA SUGESTÃO:\n\n");
			System.out.print("Digite o nome do produto: ");
			nome = entrada.nextLine();
			if(nome.trim().isEmpty())
				throw new Exception("Nome inválido.");

			System.out.print("Digite o nome da loja: ");
			loja = entrada.nextLine();

			if(loja.trim().isEmpty())
				throw new Exception("Loja inválida.");

			System.out.print("Digite o valor do produto: R$ ");
			valor = entrada.nextFloat();
			entrada.nextLine();

			System.out.print("Digite a observação do produto: ");
			observacoes = entrada.nextLine();

			if(observacoes.trim().isEmpty())
				throw new Exception("Observação inválida.");

			//Verificar se o usuario realmente deseja incluir a sugestão no sistema:
			if(Utils.askQuestion("Deseja adicionar a sugestão ao sistema?", entrada)){
				//Criar um objeto:
				Sugestao s = new Sugestao(-1,logged,nome,loja,valor,observacoes);
				//Inserir na base de dados:
				int id = banco.create(s);
				indice.create(logged,id);
				System.out.println("Sugestão criada com sucesso.");
			} else{
				System.out.println("Sugestão cancelada com sucesso.");
			}//end of if
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao incluir sugestão: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of catch
	}//end of incluirSugestao

	//Método que mostra todas as sugestões dos usuarios:
	private void listar(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.printf("MINHAS SUGESTÕES:\n\n");
			//Pegar todas as sugestões do usuario logado
			int[] idSugestoes = indice.read(logged);
			//Mostrar na tela:
			for(int i = 0; i < idSugestoes.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(idSugestoes[i]));
			}//end of for
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao listar sugestões: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of catch
	}//end of listarSugestao

	//Alterar as informações de uma determinada sugestão:
	private void alterar(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.printf("ALTERAR SUGESTÕES:\n\n");
			//Mostrar todas as sugestões na tela
			int[] id = indice.read(logged);
			for(int i =0; i < id.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(id[i]));
			}//end of for
			System.out.print("Digite a sugestão a ser alterada: ");
			//Pegar e verificar qual sugestão o usuário deseja alterar:
			int sugestao = entrada.nextInt();
			entrada.nextLine();
			sugestao -= 1;
			if(sugestao < 0 | sugestao > id.length - 1)
				throw new Exception("Sugestão inválida");

			//Pegar todas as informações a serem alteradas:
			Sugestao s = banco.read(id[sugestao]);	
			String nome,loja,observacoes;
			float valor;

			System.out.println("Digite no campo que deseja a ser alterado.");
			System.out.println("O que não deseja ser alterado basta deixar em branco.");

			System.out.print("Digite o nome do produto: ");
			nome = entrada.nextLine();
			if(nome.trim().isEmpty())
				nome = s.getNome();
			
			
			System.out.print("Digite o nome da loja: ");
			loja = entrada.nextLine();
			if(loja.trim().isEmpty())
				loja = s.getNome();
			
			
			valor = Utils.getFloat("Digite o valor do produto: R$ ", entrada);
			if(valor < 0)
				valor = s.getValor();
			

			System.out.print("Digite a observação do produto: ");
			observacoes = entrada.nextLine();
			if(observacoes.trim().isEmpty())
				observacoes = s.getObservacoes();
			
			//Perguntar se o usuario realmente deseja alterar a sua sugestão:
			if(Utils.askQuestion("Deseja realmente alterar a sugestão?",entrada)){
				s = new Sugestao(id[sugestao],logged,nome,loja,valor,observacoes);
				banco.update(s);
				System.out.printf("Sugestão alterada com sucesso.\n");
			} else {
				System.out.println("Cancelado com êxito.");
			}//end of if
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao alterar sugestão: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of catch
	}//end of alterarSugesta

	//Excluir a sugestão de um usuario:
	private void excluir(Scanner entrada){
		try{
			Utils.limparTela();
			System.out.printf("EXCLUIR SUGESTÕES:\n\n");
			//Mostrar todas as sugestões na tela:
			int[] id = indice.read(logged);
			for(int i =0; i < id.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(id[i]));
			}//end of for
			System.out.print("Digite a sugestão a ser excluída: ");
			//Pegar e verificar a sugestão a ser deletada
			int sugestao = entrada.nextInt();
			entrada.nextLine();
			sugestao -= 1;
			if(sugestao < 0 | sugestao > id.length - 1)
				throw new Exception("Sugestão inválida");

			//Verificar se o usuario realmente deseja excluir a sugestão
			if(Utils.askQuestion("Deseja realmente deletar esta sugestão?", entrada)){
				//Deletar a sugestão no banco de dados:
				indice.delete(logged,id[sugestao]);
				banco.delete(id[sugestao]);
				System.out.println("Sugestão deletada com êxito.");
			} else {
				System.out.println("Cancelado com êxito.");
			}//end of if 
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.print("Erro ao excluir sugestão: ");
			e.printStackTrace();
			Utils.travarTela(entrada);
		}//end of catch
	}//end of excluirSugestao
	
	//Método que mostra na tela todos as sugesões de um usuario:
	public void mostrar(int token){
		try{
			int[] idSugestoes = indice.read(token);
			//Mostrar na tela:
			for(int i = 0; i < idSugestoes.length; i++){
				System.out.print(i+1 + ". ");
				System.out.println(banco.read(idSugestoes[i]));
			}//end of for
		} catch(Exception e) {
			System.out.println("Erro ao mostrar sugestões.");
			e.printStackTrace();
		}//end of try
	}//end of void mostrar
}//end of Sugestoes