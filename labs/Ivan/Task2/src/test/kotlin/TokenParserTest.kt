import Parser.Token
import Parser.TokenParser
import Parser.TokenType
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.math.sqrt

class TokenParserTester{
    @Test
    @DisplayName("TokenParserTest")
    fun tokenParserTest(){
        val s = "a + 1 + (12) + Min(ac, b)"
        val parser = TokenParser(s)
        assertEquals(
            listOf(TokenType.VAR, TokenType.ADD, TokenType.NUM, TokenType.ADD, TokenType.L_PARENTHESIS, TokenType.NUM, TokenType.R_PARENTHESIS,
                        TokenType.ADD, TokenType.MIN, TokenType.L_PARENTHESIS, TokenType.VAR, TokenType.COMMA, TokenType.VAR, TokenType.R_PARENTHESIS),
            parser.parse().map { u -> u.type }
        )

        var s2 = "Man + Max(a, b)"
        var parser2 = TokenParser(s2)
        assertEquals(
            listOf(TokenType.VAR, TokenType.ADD, TokenType.MAX, TokenType.L_PARENTHESIS, TokenType.VAR, TokenType.COMMA, TokenType.VAR,
                        TokenType.R_PARENTHESIS),
            parser2.parse().map { u -> u.type }
        )
    }
}