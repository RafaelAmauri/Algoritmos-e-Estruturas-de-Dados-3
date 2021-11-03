package Interfaces;

import DataBase.*;
import Props.Mensagem;
import java.util.Scanner;
import Utils.Utils;

public class Mensagens {
    private CRUD<Mensagem> banco;
    private ArvoreBMais_Int_Int indice;
    private final String diretorio = "dados";
	private String dataFormato;
    private int logged;

    //Construtor da interface, inicializa o banco de dados, o indice e a lista invertida.
    public Mensagens(String nomeArquivo){
        try{
            banco   = new CRUD<>(nomeArquivo + " Mensagem", Mensagem.class.getConstructor());
			indice    = new ArvoreBMais_Int_Int(10,diretorio+ "/Mensagem"+nomeArquivo+".idx");
            dataFormato       = "dd/MM/yyyy HH:mm";
        }catch(Exception e){
            System.out.println("Erro ao criar interface de Grupos.");
        }//end of catch
    }//end of Grupos

    //Método que altera o usuario que está atualmente logado no sistema:
    public void changeToken(int token){
        logged = token;
    }//end of changeToken

    //Menu principal de leitura ou escrita de mensagens:
    //Não é necessário muitas explicações.
    public void tela(int idGrupo, String nomeGrupo,Scanner entrada, Usuarios usuarios){
        boolean end = false;
        while(!end){
            Utils.limparTela();
            System.out.println("BEM VINDO A TELA DE MENSAGENS DO GRUPO \"" + nomeGrupo.toUpperCase() + "\"\n");
            System.out.println("1) Visualizar mensagens.");
            System.out.println("2) Escrever uma nova mensagem.\n");
            System.out.println("0) Retornar.");
            System.out.print("Digite a operação a ser feita: ");
            int opcao = entrada.nextInt();
            entrada.nextLine();
            switch(opcao){
                case 0:
                    end = true;
                    break;
                case 1:
                    visualizar(idGrupo, nomeGrupo, entrada, usuarios);
                    break;
                case 2:
                    escrever(idGrupo, nomeGrupo, entrada);
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }//end of switch
        }//end of while
    }//end of tela

    //Menu de visualização de mensagens:
    public void visualizar(int idGrupo, String nomeGrupo, Scanner entrada, Usuarios usuarios){
        try{
            System.out.println("MENSAGENS DO GRUPO: \""+ nomeGrupo.toUpperCase() + "\"\n");
            //Obter todos os ids de mensagens do grupo:
            int[] ids = indice.read(idGrupo);
            //Verificar a existência de mensagens:
            if(ids.length <= 0){
                System.out.println("Infelizmente o grupo não tem nenhuma mensagem ainda.");
                Utils.travarTela(entrada);
            } else {
                //Criar um vetor com todas as mensagens:
                Mensagem[] m = new Mensagem[ids.length];
                for(int i = 0; i < ids.length; i++){
                    m[i] = banco.read(ids[i]);
                }//end of for
                boolean end = false;
                int toRender = 1;               //Variável responsável pelo a amostra de mensagens na tela.
                boolean proximo;
                boolean anterior;
                while(!end){
                    //Renderizar/Mostrar na tela apenas 5 mensagens por vez
                    Utils.limparTela();
                    System.out.println("MENSAGENS DO GRUPO: \""+ nomeGrupo.toUpperCase() + "\"\n");
                    proximo = false;
                    anterior = false;
                    //Laço para mostrar o bloco atual de mensagens:
                    for(int i = (toRender - 1)*5; i < toRender * 5 && i < m.length; i++){
                        //Obter todas as informações das mensagens e mostra-las na tela:
                        String nome = usuarios.getNome(m[i].getIDUsuario());
                        String email = usuarios.getEmail(m[i].getIDUsuario());
                        String data = Utils.dateToString(m[i].getMomento(), dataFormato);
                        String mensagem = m[i].getMensagem();
                        System.out.println("Remetente: " + nome + " - " + email + " às " + data);
                        System.out.println(mensagem + "\n");
                    }//end of for
                    //Verificar se o usario deseja sair ou ir para o próximo bloco ou o anterior:
                    int opcao;
                    System.out.println("0) Retornar.");
                    //Verificar se é possível ir para o bloco anterior:
                    if(toRender > 1){
                        System.out.println("1) Anterior");
                        anterior = true;
                    //Verificar se é possível ir para o próximo bloco:
                    } else if((float)toRender < (float)m.length/(float)5){
                        System.out.println("2) Próximo");
                        proximo = true;
                    }//end of if
                    //De acordo com as opções válidas alterar a variável de renderização:
                    System.out.print("\nOpção: ");
                    opcao = entrada.nextInt();
                    entrada.nextLine();
                    switch(opcao){
                        case 0:
                            end = true;
                            break;
                        case 1:
                            //Atualizar para bloco anterior:
                            if(anterior)
                                toRender -= 1;
                            break;
                        case 2:
                            //Atualizar para o próximo bloco:
                            if(proximo)
                                toRender += 1;
                            break;
                        default:
                            System.out.println("Opção Inválida.");
                            break;
                    }//end of switch
                }//end of while
            }//end of if
        } catch(Exception e) {
            System.out.println("Erro ao mostar mensagens.");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of try
    }//end of visualizar

    //Método para escrever uma nova mensagem para o grupo:
    public void escrever(int idGrupo, String nomeGrupo, Scanner entrada){
        try{
            //Obter a mensagem do usuario:
            System.out.println("ESCREVA UMA NOVA MENSAGEM PARA O GRUPO \"" + nomeGrupo.toUpperCase() + "\"");
            System.out.print("Mensagem: ");
            String mensagem = entrada.nextLine();
            //Verificar se é valida:
            if(mensagem.trim().isEmpty())
                throw new Exception("Mensagem inválida.");
            
            //Criar uma nova mensagem no banco de dados:
            Mensagem m = new Mensagem(-1, logged, idGrupo, Utils.longDataAtual(dataFormato), mensagem);
            int id = banco.create(m);
            indice.create(idGrupo, id);
            System.out.println("Mensagem enviada com sucesso.");
            Utils.travarTela(entrada);
        } catch(Exception e) {
            System.out.println("Erro ao escrever mensagem.");
            e.printStackTrace();
            Utils.travarTela(entrada);
        }//end of catch
    }//end of escrever
}//end of mensagens