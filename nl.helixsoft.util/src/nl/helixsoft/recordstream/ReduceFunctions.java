package nl.helixsoft.recordstream;

public abstract class ReduceFunctions 
{
	public static final BiFunction<Object, Object, Object> FIRST = new BiFunction<Object, Object, Object>() {

		@Override
		public Object apply(Object chain, Object more) 
		{
			if (chain == null)
			{
				return more;
			}
			else
			{
				return chain;
			}
		}
	};
	
	
	public static BiFunction<Integer, Integer, Integer> INT_SUM = new BiFunction<Integer, Integer, Integer>() {

		@Override
		public Integer apply(Integer chain, Integer more) 
		{
			if (chain == null)
			{
				return more;
			}
			if (more == null)
			{
				return chain;
			}
			return chain + more;
		}
	
	};
		
}
