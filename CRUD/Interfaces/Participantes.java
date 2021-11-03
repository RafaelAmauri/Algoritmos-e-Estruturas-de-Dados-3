package Interfaces;

import Props.Participante;
import Props.Convite;

import java.util.Random;
import Utils.Utils;

import DataBase.*;

/*
    Classe responsável pelo gerenciamento do banco de dados de participantes e algumas de suas,
    operações que não dependem de outros gerenciadores.
 */
public class Participantes {
    private CRUD<Participante> banco;
    private ArvoreBMais_Int_Int listaGrupos;
    private ArvoreBMais_Int_Int listaUsuario;
    private final String diretorio = "dados";

    //Contrutor padrão da classe:
    public Participantes(String nomeArquivo){
        try{
            //Inicializa todos os bancos de dados necessários:
            banco = new CRUD<>(nomeArquivo + "Participantes", Participante.class.getConstructor());
            listaGrupos = new ArvoreBMais_Int_Int(10, diretorio + "/ParticipantesGrupos" + nomeArquivo + ".idx");
            listaUsuario = new ArvoreBMais_Int_Int(10, diretorio + "/ParticipantesUsuarios" + nomeArquivo + ".idx");
        } catch(Exception e) {
            System.out.println("Erro ao criar interface de participantes.");
        }//end of try
    }//end of constructor

    //Método para a inclusão de um novo usuário
    public void incluir(Convite c, int id){
        try{
            //Pegar os dados do convite e do usuario e adicionados na base de dados:
            Participante p = new Participante(-1,id,c.getIDGrupo(),-1);
            int iP = banco.create(p);
            listaGrupos.create(c.getIDGrupo(),iP);
            listaUsuario.create(id,iP);
        } catch(Exception e) {
            System.out.println("Erro ao incluir participante.");
            e.printStackTrace();
        }//end of try
    }//end of incluir

    //Método que retorna todos os participantes de um determinado grupo:
    public Participante[] listar(int idGrupo){
        Participante[] p = null;            //Vetor de retorno, inicializado com nulo.
        try{
            //Ler todos os ids do participantes do grupo:
            int[] ids = listaGrupos.read(idGrupo);
            //Verificar se existe algum participante dentro do grupo:
            if(ids.length > 0){
                //Adicionar os participantes dentro do vetor de retorno:
                p = new Participante[ids.length];
                for(int i = 0; i<ids.length; i++){
                    p[i] = banco.read(ids[i]);
                }//end of for
            }//end of if
        }catch(Exception e){
            //ERRO
        }//end of try
        return p;
    }//end of listar

    //Método de remoção de um participante dentro da estrutura:
    public void remover(int id) throws Exception{
        //Ler o id de participante na base de dados:
        Participante p = banco.read(id);
        if( p == null ){
            System.out.println("Participante já foi removido, ou não existe.");
        } else {
            //Verificar qual participante tem o removido como amigo:
            Participante amigo = null;
            int[] ids = listaGrupos.read(p.getIDGrupo());
            boolean checked = false;
            for(int i = 0; i < ids.length; i++){
                amigo = banco.read(ids[i]);
                if( amigo.getIDAmigo() == p.getIDUsuario()){
                    i = ids.length;
                    checked = true;
                }//end of if
            }//end of for
            //Passar o amigo do removido par ao amigo do amigo:
            if(checked){
                amigo.setIDAmigo(p.getIDAmigo());
                banco.update(amigo);
            } //end of if
            //Deletar o participante da estrutura:
            banco.delete(id);
            listaGrupos.delete(p.getIDGrupo(),id);
            listaUsuario.delete(p.getIDUsuario(), id);
            p = null;
            amigo = null;
            ids = null;
        }//end of if
    }//end of remover

    //Método que embarlha e sorteia todos os participantes de um determinado grupo.
    public void embaralhar(int idGrupo) throws Exception{
        Utils.limparTela();
        //Obter o id de todos os participantes do grupo:
        int[] ids = listaGrupos.read(idGrupo);
        //Verificar se existe mais de um participante no grupo:
        if(ids.length <= 1)
            throw new Exception("Grupo possui numero insuficiente de participantes.");

        //Criar um vetor de participante e adicionar todos os participantes do grupo dentro dele:
        Participante[] p = new Participante[ids.length];
        for(int i = 0; i < ids.length; i++){
            p[i] = banco.read(ids[i]);
        }//end of for

        //Aleatorizar o vetor
        int j = 0;
        Random random = new Random();
        Participante aux;
        for(int i = 0; i < p.length - 1; i++){
            //Gerar um numero aleatório:
            j = random.nextInt(p.length);
            //Trocar a posição do número aleatório com a do id atual:
            aux = p[i];
            p[i] = p[j];
            p[j] = aux;
        }//end of for
        aux = null;
        //Linkar o id amigo com o próximo id do vetor:
        for(int i = 0; i < p.length -1; i++){
            p[i].setIDAmigo(p[i+1].getIDUsuario());
        }//end of for
        //Linkar o id amigo com o primeiro item do vetor:
        p[p.length -1].setIDAmigo(p[0].getIDUsuario());
        
        //Atualizar todos os participantes do grupo:
        for(int i = 0; i < p.length; i++){
            banco.update(p[i]);
        }//end of for
    }//end of embaralhar

    //Método que retorna o id de grupo do usuário:
    public int[] getGrupos(int id){
        int[] groups = null;
        try{
            //Ler todos os ids de participante do usuario:
            int[] ids = listaUsuario.read(id);
            //Verificar se o usuario participa de algum grupo:
            if( ids == null )
                throw new Exception("Usuario não participa de nenhum grupo.");
            
            groups = new int[ids.length];
            //Ler todos os ids de grupo do usuario:
            for(int i = 0; i < ids.length; i++){
                groups[i] = banco.read(ids[i]).getIDGrupo();
            }//end of for
        } catch(Exception e) {
            e.printStackTrace();
        }//end of try
        return groups;
    }//end of getGrupos

    //Método que dado o id de usuario e o id grupo retornar o participante:
    public Participante getParticipante(int id, int idGrupo) throws Exception{
        //Ler todos os ids dos usuarios:
        int[] ids = listaUsuario.read(id);
        Participante p = null;
        boolean checked = false;
        //Verificar qual o participante tem o mesmo id de grupo que o id desejado:
        for(int i = 0; i < ids.length; i++){
            p = banco.read(ids[i]);
            if( p.getIDGrupo() == idGrupo ){
                i = ids.length;
                checked = true;
            }//end of if
        }//end of for
        //Se não houver nenhum participante com o id grupo desejado.
        if(!checked)
            throw new Exception("Participante não existe.");
        
        return p;
    }//end of getParticipante
}//end of Partivipantes