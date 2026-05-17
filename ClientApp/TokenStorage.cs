namespace ClientApp
{
    public static class TokenStorage
    {
        public static string AccessToken { get; set; } = string.Empty;
        public static string RefreshToken { get; set; } = string.Empty;
    }
}
