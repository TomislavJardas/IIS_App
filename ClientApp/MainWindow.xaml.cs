using System;
using System.Windows;

namespace ClientApp
{
    public partial class MainWindow : Window
    {
        private readonly Services.PlayerService _players = new Services.PlayerService();

        public MainWindow()
        {
            InitializeComponent();
        }

        private async void LoadPlayers_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                var list = await _players.GetPlayersAsync();
                PlayersGrid.ItemsSource = list;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }
    }
}
