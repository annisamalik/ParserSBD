package parsersbd;

/**
 *
 * @author Asus
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 *
 * @author Asus
 */
public class PDA {
    private ArrayList<TransisiPDA> transisi;
    private String state;
    private String finalS;
    private Stack<String> stack;

    public Stack<String> getStack() {
        return stack;
    }
    
    public class TransisiPDA{
        private String currentS;
        private String baca;
        private String pop;
        private String push;
        private String nextS;

        public TransisiPDA(String currentS, String baca, String pop, String push, String nextS) {
            this.currentS = currentS;
            this.baca = baca;
            this.pop = pop;
            this.push = push;
            this.nextS = nextS;
        }

        public String getCurrentS() {
            return currentS;
        }

        public String getBaca() {
            return baca;
        }

        public String getPop() {
            return pop;
        }

        public String getPush() {
            return push;
        }

        public String getNextS() {
            return nextS;
        }
    }

    public PDA() {
        this.finalS = "f";
        this.stack = new Stack();
        this.state = "i";
        this.transisi = new ArrayList<TransisiPDA>();
    }
    
    public void tambahTransisi(String currentS, String baca, String pop, String push, String nextS){
        this.transisi.add(new TransisiPDA(currentS, baca, pop, push, nextS));
    }
    
    public String next(String currentS, String baca, String top){
        for (TransisiPDA t : transisi){
            if (t.getCurrentS().equals(currentS) && t.getBaca().equals(baca) && t.getPop().equals(top) && t.getPush().equals(baca) && !baca.equals("") && !top.equals("")){
                //System.out.println("Current State : "+currentS+" Baca : "+baca+" Pop : "+t.getPop()+" Push : "+t.getPush()+" Next State : "+t.getNextS());
                this.stack.pop();
                this.stack.push(baca);
                return t.getNextS();
            }else if (t.getCurrentS().equals(currentS) && t.getBaca().equals(baca) && t.getPop().equals("") && t.getPush().equals(baca)){
                //System.out.println("Current State : "+currentS+" Baca : "+baca+" Pop : "+t.getPop()+" Push : "+t.getPush()+" Next State : "+t.getNextS());
                this.stack.push(baca);
                return t.getNextS();
            }else if (t.getCurrentS().equals(currentS) && t.getBaca().equals("") && t.getPush().equals("") ){
                //System.out.println("Current State : "+currentS+" Baca : "+baca+" Pop : "+t.getPop()+" Push : "+t.getPush()+" Next State : "+t.getNextS());
                this.stack.pop();
                return t.getNextS();
            }
        }
        return currentS;
        
    }
    
    public String cariState(String baca, String pop){
        String output = "";
        for(TransisiPDA t : this.transisi){
            if (t.getBaca().equals(baca) && t.getPop().equals(pop)){
                output = t.getNextS();
            }
        }
        return output;
    }
    
    public void tampilSemua(){
        for (TransisiPDA t : transisi){
            System.out.println( "Current State : "+t.getCurrentS() +" Baca :"+t.getBaca() +" Pop :"+t.getPop()+" Push :"+t.getPush() +" Next :"+t.getNextS() );
        }
    }

    public String getState() {
        return state;
    }
    
    public String cek(List<String> query){
        this.state = "i";
        stack.add("#");  
        this.state = "q1";
        int i = 0;
        //System.out.println("Current State : i  Baca :  Pop :  Push : #  Next State : "+this.state);
        while (i<query.size() && this.state!="end"){
            this.state = next(state, query.get(i), this.stack.peek());
            i++;
        }

        //System.out.println(this.state);
        if (this.state=="end"){
            this.state = next(state, "", this.stack.peek());
            this.state = next(state, "", this.stack.peek());
            if (this.stack.empty() && this.state.equals("f")){
                return ("Query VALID");
            }else{
                if (!query.get(query.size()-1).equals(";")) return ("Query Error karena tidak diakhiri ;");
                else return ("Query tidak VALID");
            }
        }else{
            return ("Query tidak valid"+this.state);
        }
    }  
   
    public boolean valid(List<String> query){
        this.state = "i";
        stack.add("#");  
        this.state = "q1";
        int i = 0;
        //System.out.println("Current State : i  Baca :  Pop :  Push : #  Next State : "+this.state);
        while (i<query.size() && this.state!="end"){
            this.state = next(state, query.get(i), this.stack.peek());
            i++;
        }

        if (this.state.equals("end")){
            this.state = next(state, "", this.stack.peek());
            this.state = next(state, "", this.stack.peek());
            if (this.stack.empty() && this.state.equals("f")){
                return true;
            }else
                return false;
        }else
            return false;
    }
    public void reset(){
        this.state = "i";
        this.finalS = "";
        this.stack.removeAll(stack);
    }
}

