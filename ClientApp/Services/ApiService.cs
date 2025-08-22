using ClientApp.Models;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace ClientApp
{
    public class ApiService
    {
        private readonly HttpClient _http;
        public string? AccessToken { get; private set; }
        public string? RefreshToken { get; private set; }
        public string? RapidApiKey { get; set; }

        public ApiService(string baseUrl)
        {
            _http = new HttpClient { BaseAddress = new Uri(baseUrl) };
        }

        public async Task<bool> LoginAsync(string username, string password)
        {
            var payload = new { username, password };
            var response = await _http.PostAsync("/api/auth/login", new StringContent(JsonSerializer.Serialize(payload), Encoding.UTF8, "application/json"));
            if (!response.IsSuccessStatusCode) return false;
            var json = await response.Content.ReadAsStringAsync();
            using var doc = JsonDocument.Parse(json);
            AccessToken = doc.RootElement.GetProperty("accessToken").GetString();
            RefreshToken = doc.RootElement.GetProperty("refreshToken").GetString();
            return true;
        }

        public async Task RefreshAsync()
        {
            if (RefreshToken == null) return;
            var payload = new { refreshToken = RefreshToken };
            var response = await _http.PostAsync("/api/auth/refresh", new StringContent(JsonSerializer.Serialize(payload), Encoding.UTF8, "application/json"));
            response.EnsureSuccessStatusCode();
            var json = await response.Content.ReadAsStringAsync();
            using var doc = JsonDocument.Parse(json);
            AccessToken = doc.RootElement.GetProperty("accessToken").GetString();
        }

        private void ApplyHeaders()
        {
            _http.DefaultRequestHeaders.Authorization = AccessToken != null ? new AuthenticationHeaderValue("Bearer", AccessToken) : null;
            if (!string.IsNullOrWhiteSpace(RapidApiKey))
                _http.DefaultRequestHeaders.Add("X-RapidAPI-Key", RapidApiKey);
        }

        public async Task<List<Player>> GetPlayersAsync(string? name, string? team, string? season)
        {
            ApplyHeaders();
            var url = $"/players?name={name}&team={team}&season={season}";
            var json = await _http.GetStringAsync(url);
            return JsonSerializer.Deserialize<List<Player>>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true }) ?? new();
        }

        public async Task<string> UploadPlayersXmlAsync(string filePath)
        {
            ApplyHeaders();
            using var content = new StringContent(await System.IO.File.ReadAllTextAsync(filePath), Encoding.UTF8, "application/xml");
            var response = await _http.PostAsync("/validateAndSaveXml", content);
            return await response.Content.ReadAsStringAsync();
        }

        public async Task<List<Player>> GetAllPlayersAsync()
        {
            ApplyHeaders();
            var json = await _http.GetStringAsync("/api/players");
            return JsonSerializer.Deserialize<List<Player>>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true }) ?? new();
        }

        public async Task<Player?> GetPlayerAsync(int id)
        {
            ApplyHeaders();
            var json = await _http.GetStringAsync($"/api/players/{id}");
            return JsonSerializer.Deserialize<Player>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
        }

        public async Task<Player?> CreatePlayerAsync(Player p)
        {
            ApplyHeaders();
            var response = await _http.PostAsync("/api/players", new StringContent(JsonSerializer.Serialize(p), Encoding.UTF8, "application/json"));
            var json = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<Player>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
        }

        public async Task UpdatePlayerAsync(int id, Player p)
        {
            ApplyHeaders();
            await _http.PutAsync($"/api/players/{id}", new StringContent(JsonSerializer.Serialize(p), Encoding.UTF8, "application/json"));
        }

        public async Task DeletePlayerAsync(int id)
        {
            ApplyHeaders();
            await _http.DeleteAsync($"/api/players/{id}");
        }
    }
}
