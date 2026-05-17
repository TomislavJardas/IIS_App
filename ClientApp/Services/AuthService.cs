using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace ClientApp.Services
{
    public class AuthService
    {
        private readonly HttpClient _client = new HttpClient { BaseAddress = new Uri("http://localhost:8080") };

        public async Task<bool> Login(string username, string password)
        {
            var payload = new { username, password };
            var json = JsonConvert.SerializeObject(payload);
            var resp = await _client.PostAsync("/api/auth/login", new StringContent(json, Encoding.UTF8, "application/json"));
            if (!resp.IsSuccessStatusCode) return false;
            var body = await resp.Content.ReadAsStringAsync();
            var token = JsonConvert.DeserializeObject<TokenResponse>(body);
            TokenStorage.AccessToken = token.AccessToken;
            TokenStorage.RefreshToken = token.RefreshToken;
            return true;
        }

        private class TokenResponse
        {
            [JsonProperty("accessToken")] public string AccessToken { get; set; } = string.Empty;
            [JsonProperty("refreshToken")] public string RefreshToken { get; set; } = string.Empty;
        }
    }
}
