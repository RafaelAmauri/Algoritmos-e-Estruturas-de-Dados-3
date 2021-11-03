package Interfaces;
import java.util.Scanner;
import java.util.Arrays;
import DataBase.*;
import Props.*;
import Utils.Utils;

/*	Interface de usuarios
 */
public class Usuarios {
    private CRUD<Usuario> banco;

    public Usuarios(String nomeArquivo){
        try{
            banco = new CRUD<>(nomeArquivo + " Usuario", Usuario.class.getConstructor());
        } catch ( Exception e ) {
			System.out.println("Erro ao criar interface de Grupos");
			e.printStackTrace();
		}//end of catch
    }//end of constructor

	//Método que acessa a base de dados e verifica se o email está cadastro, e a senha está correta
	//caso não esteja ele retorna -1.
    public int acessar(Scanner entrada){
        int id = -1;
		try{
			Utils.limparTela();
			//Obter o email
			System.out.println("Acesso ao sistema:");
			System.out.print("Email: ");
			String string;
			string = entrada.nextLine();
			Usuario u = banco.read(string);
			
			//Verificar se ele existe na base de dados:
			if(u == null)
				throw new Exception("Não existe este usuário na base de dados.");

			//Obter a senha:
			System.out.print("Digite sua senha: ");
			String senha = entrada.nextLine();

			//Verificar com a senha existente na base de dados:
			SaltedHash table = new SaltedHash(senha, u.getSalt());
			byte[] hashed_passwd = table.get_passwd();

			if(! Arrays.equals(u.getSenha(), hashed_passwd))
				throw new Exception("Senha inválida");
			
			System.out.printf("Acesso ao usuário foi um sucesso.\n\nRedirecionando a tela principal....\n\n");
			id = u.getID();
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.println("Erro ao acessar o usuario. ");
			Utils.travarTela(entrada);
		}//end of catch
        return id;
	}//end of acesso
	
	//Método que cria um novo usuario
	public void criar(Scanner entrada) throws Exception{
		try{
			Utils.limparTela();
			//Pegar todos os dados do novo usuario:
			System.out.print("Digite o novo Email: ");
			String email = entrada.nextLine();
	
			if(Utils.email_is_invalido(email))
				throw new Exception("Email inválido.");
			
			if(banco.read(email) != null)
				throw new Exception("Email já cadastrado");
			
			System.out.print("Digite o novo nome: ");
			String nome = entrada.nextLine();
			
			if(nome.trim().isEmpty())
				throw new Exception("Nome inválido");
			
			System.out.print("Digite a nova senha: ");
			String senha = entrada.nextLine();
			
			if(senha.trim().isEmpty())
				throw new Exception("Senha inválida");
			
			if(senha.getBytes("UTF-8").length > 32)
				throw new Exception("A senha deve ser no maximo 32 caracteres!");
			
			SaltedHash table = new SaltedHash(senha);

			//Perguntar se o usuario deseja realmente criar um novo usuario:
			if(!Utils.askQuestion("Tem certeza que deseja criar o usuario? ", entrada)){
				throw new Exception("Não é necessário criar um novo usuário.");
			} else {
				//Criar um objeto e inseri-lo na base de dados
                Usuario u = new Usuario(-1,nome,email,table.get_passwd(), table.get_salt());
				banco.create(u);
			}//End of if
			Utils.travarTela(entrada);
		}catch(Exception e){
			System.out.println("Erro ao criar usuario, devido a este problema: ");
            e.printStackTrace();
            Utils.travarTela(entrada);
		}//end of catch
	}//end of novo

	//Método que dado o token do usuario retorna o email do usuario dentro da base de dados
	public String getEmail(int token){
		Usuario u = banco.read(token);
		String str = "";
		if( u != null )
			str = u.chaveSecundaria();
		return str;
	}//end of getEmail

	//Método que dado o token do usuario retorna o nome do usuario dentro da base de dados
	public String getNome(int token){
		Usuario u = banco.read(token);
		String str = "";
		if( u != null )
			str = u.getNome();
		return str;
	}//end of getNome
}//end of class Usuarios