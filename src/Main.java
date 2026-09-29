import controller.RasterizacaoController;
import view.Principal;

public class Main {

    public static void main(String[] args) {

        Principal principal = new Principal();

        new RasterizacaoController(principal);

        principal.setVisible(true);
    }
}