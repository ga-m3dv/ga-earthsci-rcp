/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.layer.ui.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag and drop {@link Transfer} implementation for transferring layer nodes.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
final public class LayerTransfer extends ByteArrayTransfer
{
	private static LayerTransfer instance = new LayerTransfer();
	private static final String TYPE_NAME = "layer-node-transfer-format"; //$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton gadget transfer instance.
	 */
	public static LayerTransfer getInstance()
	{
		return instance;
	}

	/**
	 * Avoid explicit instantiation
	 */
	private LayerTransfer()
	{
	}

	protected LayerTransferData fromByteArray(byte[] bytes)
	{
		if (bytes == null)
		{
			return null;
		}

		InputStream is = new ByteArrayInputStream(bytes);
		try
		{
			return LayerTransferData.load(is);
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	public byte[] toByteArray(LayerTransferData data)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try
		{
			LayerTransferData.save(data, os);
			return os.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				os.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	@Override
	protected int[] getTypeIds()
	{
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames()
	{
		return new String[] { TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData)
	{
		byte[] bytes = toByteArray((LayerTransferData) object);
		if (bytes != null)
		{
			super.javaToNative(bytes, transferData);
		}
	}

	@Override
	protected Object nativeToJava(TransferData transferData)
	{
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}
}
