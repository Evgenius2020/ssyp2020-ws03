object Executor {

        fun execute(operator : Operators, vararg const : Const) : Double?
        {
            if (const.isEmpty())
                return null

            when (operator)
            {
                Operators.INC -> return Increment(const[0]).getValue()
                Operators.DEC -> return Decrement(const[0]).getValue()
                Operators.SQR -> return Square(const[0]).getValue()
                Operators.SQRT-> return SquareRoot(const[0]).getValue()

                Operators.SUM -> return Sum(const[0], const[1]).getValue()
                Operators.SUB -> return Sub(const[0], const[1]).getValue()
                Operators.DIV -> return Div(const[0], const[1]).getValue()
                Operators.MUL -> return Mul(const[0], const[1]).getValue()
                Operators.POW -> return Pow(const[0], const[1]).getValue()

                Operators.MAX -> return Max(*const).getValue()
                Operators.MIN -> return Min(*const).getValue()
                Operators.AVG -> return Avg(*const).getValue()

                else -> return null
            }
        }

}
