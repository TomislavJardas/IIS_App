namespace ClientApp.Models
{
    public class Player
    {
        public string Name { get; set; } = string.Empty;
        public string Team { get; set; } = string.Empty;
        public int Season { get; set; }
        public double Points { get; set; }
    }
}
