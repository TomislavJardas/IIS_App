using System.Windows;
using System.Windows.Controls;
using System.Threading.Tasks;

namespace ClientApp
{
    public partial class LoginWindow : Window
    {
        private readonly ApiService _api = new ApiService("http://localhost:8080");

        public LoginWindow()
        {
            InitializeComponent();
        }

        private async void OnLogin(object sender, RoutedEventArgs e)
        {
            bool ok = await _api.LoginAsync(UsernameBox.Text, PasswordBox.Password);
            if (ok)
            {
                var main = new MainWindow(_api);
                main.Show();
                Close();
            }
            else
            {
                MessageBox.Show("Login failed", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
    }
}
