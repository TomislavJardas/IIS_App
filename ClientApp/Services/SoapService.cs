using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace ClientApp
{
    public class SoapService
    {
        private readonly HttpClient _http;
        private readonly string _endpoint;

        public SoapService(string endpoint)
        {
            _endpoint = endpoint;
            _http = new HttpClient();
        }

        public async Task<XDocument> SearchAsync(string term)
        {
            string body = $@"<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:pla='http://example.com/players'>
   <soapenv:Header/>
   <soapenv:Body>
      <pla:SearchRequest>
         <pla:searchTerm>{System.Security.SecurityElement.Escape(term)}</pla:searchTerm>
      </pla:SearchRequest>
   </soapenv:Body>
</soapenv:Envelope>";
            var content = new StringContent(body, Encoding.UTF8, "text/xml");
            var response = await _http.PostAsync(_endpoint, content);
            var xml = await response.Content.ReadAsStringAsync();
            return XDocument.Parse(xml);
        }
    }
}
