using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace ClientApp.Services
{
    public class PlayerService
    {
        private readonly HttpClient _client = new HttpClient { BaseAddress = new Uri("http://localhost:8080") };

        public async Task<List<Models.Player>> GetPlayersAsync()
        {
            var req = new HttpRequestMessage(HttpMethod.Get, "/players");
            if (!string.IsNullOrEmpty(TokenStorage.AccessToken))
                req.Headers.Authorization = new AuthenticationHeaderValue("Bearer", TokenStorage.AccessToken);

            var resp = await _client.SendAsync(req);
            resp.EnsureSuccessStatusCode();
            var body = await resp.Content.ReadAsStringAsync();
            return JsonConvert.DeserializeObject<List<Models.Player>>(body) ?? new List<Models.Player>();
        }
    }
}
