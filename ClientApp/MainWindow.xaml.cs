using Microsoft.Win32;
using System.Collections.Generic;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Xml.Linq;

namespace ClientApp
{
    public partial class MainWindow : Window
    {
        private readonly ApiService _api;
        private readonly SoapService _soap;
        private readonly WeatherServiceClient _weather;

        public MainWindow(ApiService api)
        {
            _api = api;
            _soap = new SoapService("http://localhost:8080/ws");
            _weather = new WeatherServiceClient("http://localhost:9090/RPC2");
            InitializeComponent();
        }

        private async void OnLoadPlayers(object sender, RoutedEventArgs e)
        {
            var players = await _api.GetPlayersAsync(NameFilter.Text, TeamFilter.Text, SeasonFilter.Text);
            PlayersGrid.ItemsSource = players;
        }

        private async void OnSelectXml(object sender, RoutedEventArgs e)
        {
            var dlg = new OpenFileDialog { Filter = "XML files (*.xml)|*.xml" };
            if (dlg.ShowDialog() == true)
            {
                string result = await _api.UploadPlayersXmlAsync(dlg.FileName);
                XmlResult.Text = result;
            }
        }

        private async void OnSoapSearch(object sender, RoutedEventArgs e)
        {
            var doc = await _soap.SearchAsync(SoapTerm.Text);
            SoapResult.Text = doc.ToString();
        }

        private async void OnGetWeather(object sender, RoutedEventArgs e)
        {
            var temps = await _weather.GetTemperatureAsync(CityBox.Text);
            WeatherResult.Text = string.Join("\n", temps);
        }

        private void OnApplyConfig(object sender, RoutedEventArgs e)
        {
            _api.RapidApiKey = string.IsNullOrWhiteSpace(ApiKeyBox.Text) ? null : ApiKeyBox.Text.Trim();
        }
    }
}
