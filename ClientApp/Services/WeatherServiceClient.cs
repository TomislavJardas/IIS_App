using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace ClientApp
{
    public class WeatherServiceClient
    {
        private readonly HttpClient _http;
        private readonly string _endpoint;

        public WeatherServiceClient(string endpoint)
        {
            _endpoint = endpoint;
            _http = new HttpClient();
        }

        public async Task<List<string>> GetTemperatureAsync(string city)
        {
            string body = $@"<?xml version='1.0'?>
<methodCall>
  <methodName>WeatherService.getTemperature</methodName>
  <params>
    <param><value><string>{System.Security.SecurityElement.Escape(city)}</string></value></param>
  </params>
</methodCall>";
            var response = await _http.PostAsync(_endpoint, new StringContent(body, Encoding.UTF8, "text/xml"));
            var xml = XDocument.Parse(await response.Content.ReadAsStringAsync());
            var values = new List<string>();
            foreach (var val in xml.Descendants("value"))
            {
                values.Add(val.Value);
            }
            return values;
        }
    }
}
