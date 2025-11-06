import java.util.*;
public class Game2048 {
    private static final int SIZE = 4;
    private final int[][] board = new int[SIZE][SIZE];
    private final Random rand = new Random();
    private int score = 0;

    public static void main(String[] args) {
        new Game2048().run();
    }

    private void run() {
        spawnRandom();
        spawnRandom();
        Scanner sc = new Scanner(System.in);

        while (true) {
            clearScreen();
            printBoard();
            if (hasWon()) {
                System.out.println("You reached 2048! You win! (press q to quit, or continue playing)");
            }
            if (isGameOver()) {
                System.out.println("No more moves. Game over. Final score: " + score);
                break;
            }
            System.out.print("Move (w/a/s/d, q=quit): ");
            String line = sc.nextLine().trim().toLowerCase();
            if (line.isEmpty()) continue;
            char cmd = line.charAt(0);
            boolean moved = false;
            switch (cmd) {
                case 'w': moved = moveUp(); break;
                case 's': moved = moveDown(); break;
                case 'a': moved = moveLeft(); break;
                case 'd': moved = moveRight(); break;
                case 'q': System.out.println("Quit. Final score: " + score); return;
                default: System.out.println("Invalid input."); sleep(500); continue;
            }
            if (moved) {
                spawnRandom();
            } else {
                // no change
            }
        }
        sc.close();
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int r = 0; r < SIZE; r++) {
            int[] row = board[r];
            int[] compressed = new int[SIZE];
            int idx = 0;
            // collect non-zero
            for (int c = 0; c < SIZE; c++) if (row[c] != 0) compressed[idx++] = row[c];
            // merge
            int[] merged = new int[SIZE];
            int mi = 0;
            for (int i = 0; i < idx; i++) {
                if (i + 1 < idx && compressed[i] == compressed[i + 1]) {
                    merged[mi++] = compressed[i] * 2;
                    score += compressed[i] * 2;
                    i++; // skip next
                } else {
                    merged[mi++] = compressed[i];
                }
            }
            // check if row changed
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != merged[c]) {
                    moved = true;
                    board[r][c] = merged[c];
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        mirrorHorizontally();
        boolean moved = moveLeft();
        mirrorHorizontally();
        return moved;
    }

    private boolean moveUp() {
        transpose();
        boolean moved = moveLeft();
        transpose();
        return moved;
    }

    private boolean moveDown() {
        transpose();
        boolean moved = moveRight();
        transpose();
        return moved;
    }

    private void transpose() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                int t = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = t;
            }
        }
    }

    private void mirrorHorizontally() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE / 2; j++) {
                int t = board[i][j];
                board[i][j] = board[i][SIZE - 1 - j];
                board[i][SIZE - 1 - j] = t;
            }
        }
    }

    private void spawnRandom() {
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) empties.add(new int[]{r, c});
            }
        }
        if (empties.isEmpty()) return;
        int[] cell = empties.get(rand.nextInt(empties.size()));
        // 90% chance 2, 10% chance 4
        board[cell[0]][cell[1]] = rand.nextDouble() < 0.9 ? 2 : 4;
    }

    private boolean hasWon() {
        for (int r = 0; r < SIZE; r++) for (int c = 0; c < SIZE; c++) if (board[r][c] == 2048) return true;
        return false;
    }

    private boolean isGameOver() {
        // any empty?
        for (int r = 0; r < SIZE; r++) for (int c = 0; c < SIZE; c++) if (board[r][c] == 0) return false;
        // any merges possible horizontally or vertically
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE - 1; c++) {
                if (board[r][c] == board[r][c + 1]) return false;
            }
        }
        for (int c = 0; c < SIZE; c++) {
            for (int r = 0; r < SIZE - 1; r++) {
                if (board[r][c] == board[r + 1][c]) return false;
            }
        }
        return true;
    }

    private void printBoard() {
        System.out.println("Score: " + score);
        System.out.println("+------+------+------+------+");
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                System.out.printf("|%4s ", board[r][c] == 0 ? "." : Integer.toString(board[r][c]));
            }
            System.out.println("|");
            System.out.println("+------+------+------+------+");
        }
    }

    private void clearScreen() {
        // Try ANSI clear, fallback to newlines
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows: printing many newlines
            for (int i = 0; i < 30; i++) System.out.println();
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

}
