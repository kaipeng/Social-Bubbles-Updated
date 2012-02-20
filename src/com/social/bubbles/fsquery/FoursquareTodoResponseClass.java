package com.social.bubbles.fsquery;

	public class FoursquareTodoResponseClass extends FoursquareObject{
		FoursquareTodoResponse response;
		public static class FoursquareTodoResponse extends FoursquareObject{
			private FoursquareTodoList todos;
		

			public FoursquareTodo[] getTodos() {
				if(todos!=null)
					return todos.items;
				return new FoursquareTodo[]{};
			}
			public FoursquareTodoList getTodoList() {
				return todos;
			}
			
		}
		public static class FoursquareTodoList extends FoursquareObject {
			protected int count;
			protected FoursquareTodo [] items;
		}
	
	}