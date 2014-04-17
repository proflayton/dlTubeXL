package com.brandonlayton.dlTubeXL;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class TubeList extends JList<Object>
{
	private Vector<Object> listVector;
	
	public TubeList()
	{
		this.listVector = new Vector<Object>();
		setCellRenderer(new TubeListRenderer());
		this.setBorder(new LineBorder(Color.RED));
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void addElement(Object obj)
	{
		this.listVector.addElement(obj);
		this.setListData(this.listVector);
	}
	
	public void clear()
	{
		this.listVector.clear();
		this.setListData(this.listVector);
	}
	
	public Object getElement(int pos)
	{
		return this.listVector.get(pos);
	}
	
	class TubeListRenderer implements ListCellRenderer<Object>
	{

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component component = (Component) value;
			component.setMinimumSize(new Dimension(30,40));
			System.out.println(((TubeListElement)value).image.getIconHeight());
			return component;
		}
		
	}
}
