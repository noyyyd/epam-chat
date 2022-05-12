<html>
<body>


    <p style="text-align: center">${error}</p>

    <form action="/login" method="POST" style="text-align: center">
        <input type="text" name="nick">
        <input type="hidden" name="cmd" value="login"><br><br>
        <button type="submit" name="submit">Sign in</button>
    </form>

</body>
</html>
