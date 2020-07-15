object Executor {

        fun execute(operator : Operators, vararg const : Const) : Const?
        {
            if (const.isEmpty())
                return null

            when (operator)
            {
                Operators.INC -> return Const(Increment(const[0]).doAction().getValue())
                Operators.DEC -> return Const(Decrement(const[0]).doAction().getValue())
                Operators.SQR -> return Const(Square(const[0]).doAction().getValue())
                Operators.SQRT-> return Const(SquareRoot(const[0]).doAction().getValue())

                Operators.SUM -> return Const(Sum(const[0], const[1]).doAction().getValue())
                Operators.SUB -> return Const(Sub(const[0], const[1]).doAction().getValue())
                Operators.DIV -> return Const(Div(const[0], const[1]).doAction().getValue())
                Operators.MUL -> return Const(Mul(const[0], const[1]).doAction().getValue())
                Operators.POW -> return Const(Pow(const[0], const[1]).doAction().getValue())

                Operators.MAX -> return Const(Max(*const).doAction().getValue())
                Operators.MIN -> return Const(Min(*const).doAction().getValue())
                Operators.AVG -> return Const(Avg(*const).doAction().getValue())

                else -> return null
            }
        }

}
