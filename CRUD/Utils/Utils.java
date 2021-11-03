package Utils;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/*  Métodos de auxílio auxilios para de outros métodos.
 */
public class Utils {
	//Método que faz uma pergunta de sim ou não, e retornar verdade para sim e falso para não.
    public static boolean askQuestion(String question,Scanner entrada){
		boolean toDo = false;
		boolean end = false;
		do{
			System.out.print(question);
			System.out.print(" [S/N]: ");
			String opcao = entrada.nextLine();
			opcao = opcao.toLowerCase();
			if(opcao.length() == 1 && opcao.charAt(0) == 's'){
				end = true;
				toDo = true;
			} else if (opcao.length() == 1 && opcao.charAt(0) == 'n'){
				end = true;
				toDo = false;
			} else {
				System.out.println("Opção inválida.");
			}//end of if
		}while(!end);
		return toDo;
	}//end of askQuestion

	//Método que trava a tela até o usuário pressiona enter.
	public static void travarTela(Scanner entrada){
		System.out.print("Pressione enter para continuar...");
		entrada.nextLine();
	}//end of travarTela

	//Método que retorna uma string de data
	public static String getDate(Scanner entrada,String pergunta){
		String data = "";
		System.out.print(pergunta);
		data = entrada.nextLine();
		return data;
	}//end of getData

	//Método que dada um string de data e do formato da data, retorna uma Date contendo a data no formato
	//desejado, caso a string seja inválida o que é retornado é um Date nulo.
	public static Date readDate(String data, String dataFormato){
		Date date;

		try{
			if(data.trim().isEmpty() || dataFormato.length() != data.length())
				throw new Exception("Formato de data inválido.");

			SimpleDateFormat formatado = new SimpleDateFormat(dataFormato);
			date = (Date) formatado.parse(data);
		}catch(Exception e){
			date = null;
		}//end of try
		return date;
	}//end of readData

	//Método que checa se da data passada está no futuro da data atual.
	public static boolean checkDate(Date data){
		Date atual = new Date();
		boolean futuro = false;
		if(atual.compareTo(data) <= 0)
			futuro = true;
		
		return futuro;
	}//end of checkData

	//Método que checa se da data passada está no futuro da data atual.
	public static boolean checkDate(long time){
		Date atual = new Date();
		Date data = new Date(time);
		boolean futuro = false;
		if(atual.compareTo(data) <= 0)
			futuro = true;
		
		return futuro;
	}//end of checkData

	//Método que dado a pergunta e o scanner de entrada retorna um float, se o usuario não desejar digitar
	//nada ele retorna -1 como float.
	public static float getFloat(String question, Scanner entrada){
		float entry = -1.f;
		boolean end = false;
		String string;
		while(!end){
			try{
				System.out.print(question);
				string = entrada.nextLine();
				if(string.trim().isEmpty()){
					end = true;
				} else {
					entry = Float.parseFloat(string);
					end = true;
				}//end of if
			}catch(Exception e){
				System.out.println("Valor inválido, tente novamente.");
				entry = -1;
				travarTela(entrada);
			}//end of try
		}//end of while
		return entry;
	}//end of getFloat

	//Método que dado um long de tempo e formato da data retorna um string formatada contendo a data.
	public static String dateToString(long time, String dataFormato){
		String date = "";
		try{
			SimpleDateFormat formato = new SimpleDateFormat(dataFormato);
			Date data = new Date(time);
			date = formato.format(data);
		} catch(Exception e) {
			System.out.println("Data inválida");
			e.printStackTrace();
		}//end of try
		return date;
	}//end of getDate

	//Método que retorna a data atual.
	public static String dataAtual(String dataFormato){
		String date = "";
		try{
			SimpleDateFormat formato = new SimpleDateFormat(dataFormato);
			Date data = new Date();
			date = formato.format(data);
		} catch(Exception e) {
			System.out.println("Data inválida");
			e.printStackTrace();
		}//end of try
		return date;
	}//end of dataAtual

	//Método que retorna a dataAtual
	public static long longDataAtual(String formato){
		return new Date().getTime();
	}//end of dataAtual

	//Método que limpa a tela.
	public static void limparTela(){
		try{
			new ProcessBuilder("clear").inheritIO().start().waitFor();
		} catch(Exception e) {
			//Erro
		}//end of catch
	}//end of limparTela

	// Valida se o email inserido eh valido por meio de expressoes regulares
	//Criado por Rafael Amauri.
    public static boolean email_is_invalido(String email)
	{
        Pattern regex_email  =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher      =  regex_email.matcher(email);
        return !matcher.find();
    }
}//end of checkData