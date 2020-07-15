package Parser

enum class TokenType {
    ADD, SUB, DIV, MUL, POW,
    INC, DEC,
    MIN, MAX, AVG,
    COMMA, L_PARENTHESIS, R_PARENTHESIS,
    VAR, NUM
}

class Token(var type: TokenType, var value: Double, var key: String) {
    constructor(Type: TokenType) : this(Type, 0.0, "")
    constructor(Type: TokenType, Value: Double) : this(Type, Value, "")
    constructor(Type: TokenType, Key: String) : this(Type, 0.0, Key)
}

class TokenParser(var expr: String) {
    var counter: Int = 0

    fun parse(): List<Token> {
        val result: MutableList<Token> = mutableListOf()
        while (counter < expr.length) {
            when {
                expr[counter].isLetter() -> {
                    if (counter + 2 < expr.length) {
                        when {
                            (expr[counter] == 'M' && expr[counter + 1] == 'i' && expr[counter + 2] == 'n') -> {
                                counter += 3
                                result.add(Token(TokenType.MIN))
                            }
                            (expr[counter] == 'M' && expr[counter + 1] == 'a' && expr[counter + 2] == 'x') -> {
                                counter += 3
                                result.add(Token(TokenType.MAX))
                            }
                            (expr[counter] == 'A' && expr[counter + 1] == 'v' && expr[counter + 2] == 'g') -> {
                                counter += 3
                                result.add(Token(TokenType.AVG))
                            }
                            else -> result.add(parseVarName())
                        }
                    } else {
                        result.add(parseVarName())
                    }
                }
                expr[counter].isDigit() -> {
                    result.add(parseNumber())
                }
                expr[counter] == ' ' -> {
                    counter++
                }
                else -> result.add(parseOperator())
            }
        }
        return result
    }

    private fun parseOperator(): Token{
        counter++
        return when (expr[counter - 1]) {
            '(' -> Token(TokenType.L_PARENTHESIS)
            ')' -> Token(TokenType.R_PARENTHESIS)
            '+' -> {
                if (counter < expr.length) {
                    if (expr[counter] == '+') {
                        counter++
                        Token(TokenType.INC)
                    } else {
                        Token(TokenType.ADD)
                    }
                } else {
                    Token(TokenType.ADD)
                }
            }
            '-' -> {
                if (counter < expr.length) {
                    if (expr[counter] == '-') {
                        counter++;
                        Token(TokenType.DEC)
                    } else {
                        Token(TokenType.SUB)
                    }
                } else {
                    Token(TokenType.SUB)
                }
            }
            '/' -> Token(TokenType.DIV)
            '*' -> {
                if (counter < expr.length) {
                    if (expr[counter] == '*') {
                        counter++;
                        Token(TokenType.POW)
                    } else {
                        Token(TokenType.MUL)
                    }
                } else {
                    Token(TokenType.MUL)
                }
            }
            ',' -> Token(TokenType.COMMA)

            else -> throw IllegalArgumentException("Unknown symbol $expr[counter]")
        }
    }

    private fun parseNumber(): Token {
        val builder = StringBuilder()
        while(counter < expr.length && (expr[counter].isDigit() || expr[counter] == '.')) {
            builder.append(expr[counter])
            counter++
        }
        return Token(TokenType.NUM, builder.toString().toDouble())
    }

    private fun parseVarName(): Token {
        val builder = StringBuilder()
        while(counter < expr.length && expr[counter].isLetterOrDigit()) {
            builder.append(expr[counter])
            counter++
        }
        return Token(TokenType.VAR, builder.toString())
    }
}

fun main() {
    val tokens = TokenParser("1++").parse()
    println(tokens[0].type.name)
}
