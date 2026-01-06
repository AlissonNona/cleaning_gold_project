import jason.asSyntax.*;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

public class GoldMarsEnv extends Environment {

    public static final int GSize = 10;
    public static final int GARB  = 16;
    public static final int GOLD  = 32;
    public static final int DEPOSIT = 64;

    public static final Term    ns = Literal.parseLiteral("next(slot)");
    public static final Term    pg = Literal.parseLiteral("pick(garb)");
    public static final Term    pgo = Literal.parseLiteral("pick(gold)");
    public static final Term    dg = Literal.parseLiteral("drop(garb)");
    public static final Term    dgo = Literal.parseLiteral("deposit_gold");
    public static final Term    bg = Literal.parseLiteral("burn(garb)");
    public static final Term    gg = Literal.parseLiteral("give_garbage");
    
    public static final Literal g1 = Literal.parseLiteral("garbage(r1)");
    public static final Literal go1 = Literal.parseLiteral("gold(r1)");
    public static final Literal g2 = Literal.parseLiteral("garbage(r2)");

    static Logger logger = Logger.getLogger(GoldMarsEnv.class.getName());

    private GoldMarsModel model;
    private GoldMarsView  view;

    @Override
    public void init(String[] args) {
        model = new GoldMarsModel();
        view  = new GoldMarsView(model);
        model.setView(view);
        updatePercepts();
        logger.info("Ambiente GoldMarsEnv iniciado com sucesso!");
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" executando: "+ action);
        try {
            if (action.equals(ns)) {
                model.nextSlot();
            } else if (action.getFunctor().equals("move")) {
                String direction = action.getTerm(0).toString();
                model.move(direction);
            } else if (action.equals(pg)) {
                model.pickGarb();
            } else if (action.equals(pgo)) {
                model.pickGold();
            } else if (action.equals(dg)) {
                model.dropGarb();
            } else if (action.equals(dgo)) {
                model.depositGold();
            } else if (action.equals(gg)) {
                model.giveGarbage();
            } else if (action.equals(bg)) {
                if ("r2".equals(ag)) {
                    model.burnGarb();
                } else {
                    logger.warning("Apenas R2 pode queimar lixo!");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        updatePercepts();

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
        
        informAgsEnvironmentChanged();
        return true;
    }

    void updatePercepts() {
        clearPercepts();

        addPercept(Literal.parseLiteral("pos(r1," + model.getAgPos(0).x + "," + model.getAgPos(0).y + ")"));
        addPercept(Literal.parseLiteral("pos(r2," + model.getAgPos(1).x + "," + model.getAgPos(1).y + ")"));
        
        addPercept("r1", Literal.parseLiteral("gold_deposit_pos(0,0)"));
        addPercept("r1", Literal.parseLiteral("r2_position(9,9)"));

        if (model.hasObject(GARB, model.getAgPos(0))) {
            addPercept("r1", Literal.parseLiteral("garbage_here"));
        }
        if (model.hasObject(GOLD, model.getAgPos(0))) {
            addPercept("r1", Literal.parseLiteral("gold_here"));
        }

        if (model.getAgPos(0).x == 0 && model.getAgPos(0).y == 0) {
            addPercept("r1", Literal.parseLiteral("at_gold_deposit"));
        }
        if (model.getAgPos(0).x == 9 && model.getAgPos(0).y == 9) {
            addPercept("r1", Literal.parseLiteral("at_r2"));
        }

        if (model.r1HasGarb) {
            addPercept("r1", Literal.parseLiteral("carrying(garbage)"));
        } else if (model.r1HasGold) {
            addPercept("r1", Literal.parseLiteral("carrying(gold)"));
        }
        
        if (model.r2HasGarb) {
            addPercept("r2", Literal.parseLiteral("has_garbage"));
        }
    }

    class GoldMarsModel extends GridWorldModel {

        int nerr;
        boolean r1HasGarb = false;
        boolean r1HasGold = false;
        boolean r2HasGarb = false;

        Random random = new Random();

        private GoldMarsModel() {
            super(GSize, GSize, 2);

            try {
                setAgPos(0, 0, 0);
                setAgPos(1, 9, 9);
            } catch (Exception e) {
                e.printStackTrace();
            }

            addGarbAndGold();
        }

        void addGarbAndGold() {
            add(GARB, 3, 0);
            add(GARB, 7, 2);
            add(GARB, 1, 4);
            add(GARB, 5, 5);
            add(GARB, 8, 7);
            add(GARB, 2, 9);
            
            add(GOLD, 2, 2);
            add(GOLD, 6, 1);
            add(GOLD, 4, 4);
            add(GOLD, 9, 3);
            add(GOLD, 3, 7);
            add(GOLD, 7, 8);
        }

        void move(String direction) throws Exception {
            Location r1 = getAgPos(0);
            switch (direction) {
                case "north": if (r1.y > 0) r1.y--; break;
                case "south": if (r1.y < getHeight()-1) r1.y++; break;
                case "east": if (r1.x < getWidth()-1) r1.x++; break;
                case "west": if (r1.x > 0) r1.x--; break;
            }
            setAgPos(0, r1);
        }

        void nextSlot() throws Exception {
            Location r1 = getAgPos(0);
            r1.x++;
            if (r1.x == getWidth()) {
                r1.x = 0;
                r1.y++;
            }
            if (r1.y == getHeight()) {
                r1.y = 0;
            }
            setAgPos(0, r1);
        }

        void pickGarb() {
            if (hasObject(GARB, getAgPos(0)) && !r1HasGarb && !r1HasGold) {
                remove(GARB, getAgPos(0));
                r1HasGarb = true;
                logger.info("R1 pegou lixo");
            }
        }
        
        void pickGold() {
            if (hasObject(GOLD, getAgPos(0)) && !r1HasGarb && !r1HasGold) {
                remove(GOLD, getAgPos(0));
                r1HasGold = true;
                logger.info("R1 pegou OURO");
            }
        }
        
        void dropGarb() {
            if (r1HasGarb) {
                add(GARB, getAgPos(0));
                r1HasGarb = false;
                logger.info("R1 dropou lixo");
            }
        }
        
        void depositGold() {
            if (r1HasGold && getAgPos(0).x == 0 && getAgPos(0).y == 0) {
                r1HasGold = false;
                logger.info("R1 depositou OURO no depósito");
                addPercept("gold_deposit", Literal.parseLiteral("gold_received"));
            }
        }
        
        void giveGarbage() {
            if (r1HasGarb && getAgPos(0).x == 9 && getAgPos(0).y == 9) {
                r1HasGarb = false;
                r2HasGarb = true;
                logger.info("R1 entregou lixo para R2");
                addPercept("r2", Literal.parseLiteral("garbage_delivered"));
            }
        }
        
        void burnGarb() {
            if (r2HasGarb) {
                r2HasGarb = false;
                logger.info("R2 queimou lixo");
                removePercept("r2", Literal.parseLiteral("garbage_delivered"));
            }
        }
    }

    class GoldMarsView extends GridWorldView {

        public GoldMarsView(GoldMarsModel model) {
            super(model, "Sistema de Limpeza com Ouro", 700);
            defaultFont = new Font("Arial", Font.BOLD, 14);
            setVisible(true);
            repaint();
        }

        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
                case GARB:
                    drawGarb(g, x, y);
                    break;
                case GOLD:
                    drawGold(g, x, y);
                    break;
                case DEPOSIT:
                    drawDeposit(g, x, y);
                    break;
            }
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "R" + (id + 1);
            if (id == 0) {
                c = Color.BLUE;
                if (((GoldMarsModel) model).r1HasGarb) {
                    label += "-GARB";
                    c = Color.ORANGE;
                } else if (((GoldMarsModel) model).r1HasGold) {
                    label += "-GOLD";
                    c = Color.YELLOW;
                }
            } else {
                c = Color.RED;
                if (((GoldMarsModel) model).r2HasGarb) {
                    label += "-BURN";
                }
            }
            
            super.drawAgent(g, x, y, c, -1);
            g.setColor(Color.WHITE);
            super.drawString(g, x, y, defaultFont, label);
        }

        public void drawGarb(Graphics g, int x, int y) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
            g.setColor(Color.WHITE);
            drawString(g, x, y, defaultFont, "GARB");
        }
        
        public void drawGold(Graphics g, int x, int y) {
            g.setColor(Color.YELLOW);
            g.fillOval(x * cellSizeW + 4, y * cellSizeH + 4, cellSizeW - 8, cellSizeH - 8);
            g.setColor(Color.BLACK);
            drawString(g, x, y, defaultFont, "GOLD");
        }
        
        public void drawDeposit(Graphics g, int x, int y) {
            g.setColor(Color.GREEN);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            g.setColor(Color.WHITE);
            drawString(g, x, y, defaultFont, "DEPOSIT");
        }
        
        @Override
        public void drawEmpty(Graphics g, int x, int y) {
            if (x == 0 && y == 0) {
                drawDeposit(g, x, y);
            } else {
                super.drawEmpty(g, x, y);
            }
        }
    }
}