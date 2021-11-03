import Interfaces.Interface;

public class Principal{
    public static void main(String[] args){
        Interface sistema = new Interface("BancoDeDados");
		sistema.menu();
    }//end of main
}//end of Principal