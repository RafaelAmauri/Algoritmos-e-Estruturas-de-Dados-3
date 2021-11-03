package Interfaces;
import java.util.Scanner;
import DataBase.*;
import Props.Convite;
import Utils.Utils;

import java.util.Date;

/*  Interface de convites:
 */
public class Convites {
    private CRUD<Convite> banco;
    private ArvoreBMais_Int_Int indice;
    private ArvoreBMais_ChaveComposta_String_Int listaInvertida;
    private final String diretorio = "dados";
	private String dataFormato;

    //Construtor da interface, inicializa o banco de dados, o indice e a lista invertida.
    public Convites(String nomeArquivo){
        try{
            banco   = new CRUD<>(nomeArquivo + " convite", Convite.class.getConstructor());
			indice    = new ArvoreBMais_Int_Int(10,diretorio+ "/Convite"+nomeArquivo+".idx");
            listaInvertida = new ArvoreBMais_ChaveComposta_String_Int(10, diretorio+ "/ConvitePendentes"+nomeArquivo+".idx");
            dataFormato       = "dd/MM/yyyy HH:mm";
        }catch(Exception e){
            System.out.println("Erro ao criar interface de Grupos.");
        }//end of catch
    }//end of Grupos

    //Método de listagem de convites, necessário o id do grupo referido:
    public void listar(int iG,Scanner entrada, String nomeGrupo){
        try {
            Utils.limparTela();
            //Pegar todos os convites que se relacionam com o id do grupo:
            int id[] = this.indice.read(iG);                                    //ERRO NESTA LINHA
            //Criar um arranjo de convites e inserir todos os convite de id obtidos na etapa anterior nele.
            Convite c[] = new Convite[id.length];
            for(int i = 0; i < id.length; i++){
                c[i] = banco.read(id[i]);
            }//end of for
            //Printar o nome do grupo:
            System.out.println("CONVITES DO GRUPO \"" + nomeGrupo.toUpperCase() + "\" \n");
            //Listar todos os convites na tela:
            for(int i = 0; i < id.length; i++ ){
                System.out.print( i + 1 );
                System.out.print(". " + c[i]);
                System.out.print(" (");
                System.out.print(Utils.dateToString(c[i].getMomentoConvite(), dataFormato));
                System.out.println(" - " + c[i].estadoToString() + ")");
            }//end of for
            Utils.travarTela(entrada);
        } catch (Exception e) {
            System.out.println("Erro ao listar grupo.");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of try
    }//end of listar

    //Método de emissão de convites:
    public void emitir(int iG, Scanner entrada, String nomeGrupo){
        boolean end = false;
        try{
            Utils.limparTela();
            //Enquanto o usuario desejar criar um novo convite repetir o laço:
            while(!end){
                //Printar o nome do grupo:
                System.out.println("EMISSÃO DE CONVITES DO GRUPO \"" + nomeGrupo.toUpperCase() + "\" \n");
                //Perguntar se o usuario deseja criar um novo convite:
                if(Utils.askQuestion("Deseja criar um novo convite? ", entrada)){
                    //Pegar o email e verificá-lo
                    String email;
                    System.out.print("Digite o email do participante: ");
                    email = entrada.nextLine();
                    if(Utils.email_is_invalido(email))
                        throw new Exception("Email inválido. ");
                    //Ler na base de dados e verificar se o email já existe:
                    Convite c = banco.read(email);
                    if( c == null || c.getIDGrupo() != iG){
                        //Se não existir ou o id do grupo for diferente do id de grupo deseja criar o convite:
                        Date date = new Date();
                        long mC = date.getTime();
                        c = new Convite(-1,iG,email,mC,(byte)0);
                        int id = banco.create(c);
                        listaInvertida.create(email, id);
                        indice.create(iG, id);
                    } else {
                        //Se o convite existe:
                        //Verificar o estado do convite:
                        byte estado = c.getEstado();
                        if( estado == 0 || estado == 1){
                            //Se for pendente ou aceito, não é necessário fazer nenhuma ação:
                            System.out.println("Email já tem um convite emitido.");
                        } else if(estado == 2 || estado == 3){
                            //Se for cancelado ou recusado perguntar se o usuario deseja reemitir o convite:
                            if(Utils.askQuestion("Deseja reemitir o convite para este email \"" + 
                                                 email + "\"", entrada)){
                                Date date = new Date();
                                c.setEstado((byte)0);
                                c.setMomentoConvite(date.getTime());
                                banco.update(c);
                                listaInvertida.create(c.chaveSecundaria(), c.getID());
                            }//end of if
                        } else {
                            //Senão é um estado inválido.
                            throw new Exception("Estado do convite inválido.");
                        }//end of if
                    }//end of if
                    System.out.println("Operação realizado com êxito.");
                    Utils.travarTela(entrada);
                } else {
                    end = true;
                }//end of if
            }//end of while
        } catch ( Exception e ){
            System.out.println("Erro ao emitir convite: ");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of catch
    }//end of emitir

    //Método para cancelamento de convites:
    public void cancelar(int iG, Scanner entrada, String nomeGrupo){
        try{
            Utils.limparTela();
            //Pegar todos os ids de convites relacionados ao grupos desejado:
            int id[] = indice.read(iG);                 //ERRO NESTA LINHA.
            //Criar um arranjo de convites e inserir todos os convites com ids obtidos na etapas anteriores,
            //e inseri-los no arranjos:
            Convite c[] = new Convite[id.length];
            for(int i = 0; i < id.length; i++){
                c[i] = banco.read(id[i]);
            }//end of for
            //Printar o grupo:
            System.out.println("CONVITES DO GRUPO \"" + nomeGrupo.toUpperCase() + "\" \n");
            //Printar todos os convites na tela.
            for(int i = 0; i < id.length; i++ ){
                System.out.print( i + 1 );
                System.out.print(". " + c[i]);
                System.out.print(" (");
                System.out.print(Utils.dateToString(c[i].getMomentoConvite(), dataFormato));
                System.out.println(" - " + c[i].estadoToString() + ")");
            }//end of for
            //Pegar o convite a ser cancelado:
            System.out.print("Digite o convite que deseja cancelar: ");
            int op = entrada.nextInt();
            entrada.nextLine();
            //Verificar a opção digitada:
            op -= 1;
            if( op < 0 || op > c.length - 1)
                throw new Exception("Opção inválida");
            
            byte estado = c[op].getEstado();
            if(estado == 3)
                throw new Exception("Convite já foi cancelado.");

            if(estado == 1)
                throw new Exception("Convite já foi aceito.");

            //Perguntar se o usuario deseja realmente cancelar o convite
            if(Utils.askQuestion("Deseja realmente cancelar o convite do email \"" + c[op].chaveSecundaria() +
                                 "\"", entrada)){
                //Mudar o estado e atualizar no banco de dados.
                c[op].setEstado((byte)3);
                banco.update(c[op]);
                listaInvertida.delete(c[op].chaveSecundaria(), c[op].getID());
            }//end of if
            System.out.println("Operação realizada com sucesso. ");
            Utils.travarTela(entrada);
        } catch (Exception e) {
            System.out.println("Erro ao cancelar convite: ");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of try
    }//end of cancelar

    //Método que lê da lista invertida e retorna a quantidade de convites que o usuario tem:
    public int quantConvites(String email){
        int quantidade = 0;
        try{
            int id[] = listaInvertida.read(email);
            quantidade = id.length;
        } catch(Exception e) {
            System.out.println("Erro ao recuperar quantidade de convites.");
            e.printStackTrace();
        }//end of try
        return quantidade;
    }//end of quantConvites

    //Tela auxiliar para aceitação ou recusa de um determinado com o id parametrizado.
    public void telaSecundaria(int id, int idUsuario,Scanner entrada, Participantes p){
        boolean end = false;
        try{
            //Loop de verificação de opção:
            while(!end){
                Utils.limparTela();
                //Verificar o id parametrizado:
                char opcao;
                Convite c = banco.read(id);
                if( c == null)
                    throw new Exception("ID de convite inválido.");
                
                //Pegar a opção do usuário:
                System.out.println("A - aceitar, R - recusar, V - voltar");
                System.out.print("Digite sua opcao: ");
                opcao = entrada.nextLine().charAt(0);
                //Se ele desejar aceitar:
                if( opcao == 'A' || opcao == 'a'){
                    //Atualizar estado do objeto para aceito:
                    c.setEstado((byte)1);
                    banco.update(c);
                    //Remover da arvore de relacionamento de email usuario:
                    listaInvertida.delete(c.chaveSecundaria(), id);
                    p.incluir(c,idUsuario);
                    Utils.travarTela(entrada);
                    //Finalizar o loop:
                    end = true;
                //Se ele desejar recusar:
                } else if( opcao == 'R' || opcao == 'r'){
                    //Atualizar estado do objeto para recusado:
                    c.setEstado((byte)2);
                    banco.update(c);
                    //Remover da arvore de relacionamento de email usuario:
                    listaInvertida.delete(c.chaveSecundaria(), id);
                    //Finalizar o loop:
                    end = true;
                //Se ele desejar retornar
                } else if( opcao == 'V' || opcao == 'r'){
                    //Finalizar o loop:
                    end = true;
                //Caso de opção inválida:
                } else {
                    System.out.println("Opção inválida tente novamente");
                    Utils.travarTela(entrada);
                }//end of else
            }//end of while
            System.out.println("Operação realizado com êxito.");
            Utils.travarTela(entrada);
        } catch (Exception e){
            System.out.println("Erro ao alterar convite.");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of catch
    }//end of telaSecundaria

    //Método que lê do banco de dados e retorna todos os convites do usuário:
    public Convite[] getConvites(String email){
        Convite[]  c = null;
        try{
            //Ler da lista invertida:
            int[] id = listaInvertida.read(email);
            //Criar um vetor com todos os ids:
            c = new Convite[id.length];
            for(int i = 0; i < id.length; i++){
                c[i] = banco.read(id[i]);
            }//end of for
        } catch (Exception e){
            System.out.println("Erro ao recuperar convites.");
            e.printStackTrace();
            c = null;
        }//end of catch
        return c;
    }//end of getConvites
}//end of convites