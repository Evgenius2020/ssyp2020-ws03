package Parser

import Avg
import Const
import Dec
import Div
import Expression
import Inc
import Max
import Min
import Mul
import Pow
import Sub
import Sum
import VariableContainer

/*
EXPR -> TERM|TERM + EXPR|TERM - EXPR
TERM -> FACTOR|FACTOR * TERM|FACTOR / TERM
FACTOR -> POWER|POWER ** FACTOR
POWER -> INC|-INC
INC -> ATOM++|ATOM--|ATOM
ATOM -> FUNC|NUM|VAR|(EXPR)
FUNC -> MIN(EXPRS)|MAX(EXPRS)|AVG(EXPRS)
EXPRS -> EXPR|EXPRS,EXPR
 */


class Parser(var base: List<Token>, var container: VariableContainer? = null) {
    var counter: Int = 0

    fun parse(): Expression? = parseExpression()

    private fun parseExpression(): Expression? {
        if (counter >= base.size) return null

        val lhs = parseTerm() ?: throw IllegalArgumentException("Non-parsable string")

        if(counter >= base.size) return lhs
        if (base[counter].type == TokenType.ADD || base[counter].type == TokenType.SUB) {
            var type_breakpoint = counter
            counter++
            val rhs = parseExpression() ?: throw IllegalArgumentException("Non-parsable string")
            if (base[type_breakpoint].type == TokenType.ADD) {
                return Sum(lhs, rhs)
            } else{
                return Sub(lhs, rhs)
            }
        }
        return lhs
    }

    private fun parseTerm(): Expression? {
        if (counter >= base.size) return null

        val lhs = parseFactor() ?: throw IllegalArgumentException("Non-parsable string")
        if(counter >= base.size) return lhs
        if (base[counter].type == TokenType.DIV || base[counter].type == TokenType.MUL) {
            counter++
            val rhs = parseTerm() ?: throw IllegalArgumentException("Non-parsable string")
            if (base[counter].type == TokenType.MUL) {
                return Mul(lhs, rhs)
            } else {
                return Div(lhs, rhs)
            }
        }
        return lhs
    }

    private fun parseFactor(): Expression? {
        if (counter >= base.size) return null

        val lhs = parsePower() ?: throw IllegalArgumentException("Non-parsable string")
        if(counter >= base.size) return lhs
        if (base[counter].type == TokenType.POW) {
            val rhs = parseFactor() ?: throw IllegalArgumentException("Non-parsable string")
            return Pow(lhs, rhs)
        }
        return lhs
    }

    private fun parsePower(): Expression? {
        if (counter >= base.size) return null

        val flag = (base[counter].type == TokenType.SUB)
        if (flag) counter++
        val arg = parseInc() ?: throw IllegalArgumentException("Non-parsable string")
        if (flag) return Mul(Const(-1.0), arg)
        return arg
    }

    private fun parseInc(): Expression? {
        if(counter >= base.size) return null

        val arg = parseAtom() ?: throw IllegalArgumentException("Non-parsable string")
        if(counter >= base.size) return arg
        counter++
        return when (base[counter-1].type) {
            TokenType.INC -> Inc(arg)
            TokenType.DEC -> Dec(arg)
            else -> arg
        }
    }

    private fun parseAtom(): Expression? {
        if(counter >= base.size) return null

        when (base[counter].type) {
            TokenType.NUM -> {
                counter++
                return Const(base[counter - 1].value)
            }
            TokenType.VAR -> {
                counter++
                return Const(base[counter - 1].key, container ?: throw IllegalArgumentException("There are no container"))
            }
            TokenType.L_PARENTHESIS ->{
                counter++
                val arg = parseExpression()
                if(counter >= base.size) throw IllegalArgumentException("There are no container")
                if (base[counter].type == TokenType.R_PARENTHESIS) {
                    counter++
                    return arg
                }
                throw IllegalArgumentException("There is no right parenthesis")
            }
            else -> return parseFunc() ?: throw IllegalArgumentException("Non-parsable string")
        }
    }

    private fun parseFunc(): Expression? {
        if(counter >= base.size) return null

        var type = base[counter].type
        counter++
        if(base[counter].type != TokenType.L_PARENTHESIS) {
            throw IllegalArgumentException("Non-parsable string")
        }
        counter++
        if(counter >= base.size) throw IllegalArgumentException("There are no container")
        var args = parseExprs()
        if(base[counter].type != TokenType.R_PARENTHESIS) {
            throw IllegalArgumentException("There is no right parenthesis")
        }
        return when(type){
            TokenType.MAX -> Max(args)
            TokenType.MIN -> Min(args)
            TokenType.AVG -> Avg(args)
            else -> throw IllegalArgumentException("Non-parsable string")
        }
    }

    private fun parseExprs(): List<Expression> {
        val result = mutableListOf<Expression>()
        while(counter < base.size && base[counter].type != TokenType.R_PARENTHESIS) {
            var arg = parseExpression() ?: throw IllegalArgumentException("Non-parsable string")
            result.add(arg)

            if(base[counter].type == TokenType.R_PARENTHESIS){
                return result
            }
            if(base[counter].type != TokenType.COMMA) {
                throw IllegalArgumentException("Non-parsable string")
            }
            counter++
        }
        if(base[counter].type == TokenType.R_PARENTHESIS) {
            return result;
        }
        throw IllegalArgumentException("There is no right parenthesis")
    }
}

fun main() {
    val tokens = TokenParser("1 + (2++)").parse()
    val parse = Parser(tokens).parse()
    if(parse == null){
        println(null)
    } else {
        print(Executor.execute(parse!!))
    }
}