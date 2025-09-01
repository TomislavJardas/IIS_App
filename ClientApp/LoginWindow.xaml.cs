using System.Windows;

namespace ClientApp
{
    public partial class LoginWindow : Window
    {
        private readonly Services.AuthService _auth = new Services.AuthService();

        public LoginWindow()
        {
            InitializeComponent();
        }

        private async void Login_Click(object sender, RoutedEventArgs e)
        {
            var success = await _auth.Login(UsernameBox.Text, PasswordBox.Password);
            if (success)
            {
                var main = new MainWindow();
                main.Show();
                Close();
            }
            else
            {
                MessageBox.Show("Login failed");
            }
        }
    }
}
