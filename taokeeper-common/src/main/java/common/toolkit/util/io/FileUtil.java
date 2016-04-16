package common.toolkit.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import common.toolkit.constant.EncodingConstant;
import common.toolkit.util.StringUtil;

/**
 * 类说明: 文件操作相关工具类
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class FileUtil {

	/**
	 * 将文件内容按行拆分成一个List<String>
	 * 
	 * @param fileName
	 *            文件完整路径
	 * @return List<String> 所有所有行内容的一个集合
	 * @throws Exception
	 */
	public static List< String > fileToListByLine( String fileName ) throws Exception {

		List< String > lineList = new LinkedList< String >();
		String line = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader( new FileReader( fileName ) );
			while ( ( line = in.readLine() ) != null ) {
				lineList.add( line );
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( null != in ) {
				in.close();
			}
		}
		return lineList;
	}

	/**
	 * 将文件内容按行拆分成一个List<String>
	 * 
	 * @param fileContent
	 *            文件内容
	 * @return List<String> 所有所有行内容的一个集合
	 * @throws Exception
	 */
	public static List< String > stringToListByLine( final String fileContent ) throws Exception {

		List< String > lineList = new LinkedList< String >();

		for ( String line : fileContent.split( "\n" ) ) {
			lineList.add( line );
		}

		return lineList;
	}

	// list sorted files
	public static File[] listSortedFiles( File dirFile ) {

		assert dirFile.isDirectory();

		File[] files = dirFile.listFiles();

		FileWrapper[] fileWrappers = new FileWrapper[files.length];
		for ( int i = 0; i < files.length; i++ ) {
			fileWrappers[i] = new FileWrapper( files[i] );
		}

		Arrays.sort( fileWrappers );

		File[] sortedFiles = new File[files.length];
		for ( int i = 0; i < files.length; i++ ) {
			sortedFiles[i] = fileWrappers[i].getFile();
		}

		return sortedFiles;
	}

	// list sorted files, with special fileName filter
	public static File[] listSortedFiles( File dirFile, final String fileNameFilter ) {

		assert dirFile.isDirectory();

		File[] files = dirFile.listFiles( new FilenameFilter() {

			public boolean accept( File dir, String name ) {

				if ( StringUtil.isBlank( name ) ) {
					return false;
				}

				// Must statistics history file
				if ( name.startsWith( fileNameFilter ) && !name.endsWith( "log" ) ) {
					return true;
				}
				return false;
			}
		} );

		FileWrapper[] fileWrappers = new FileWrapper[files.length];
		for ( int i = 0; i < files.length; i++ ) {
			fileWrappers[i] = new FileWrapper( files[i] );
		}

		Arrays.sort( fileWrappers );

		File[] sortedFiles = new File[files.length];
		for ( int i = 0; i < files.length; i++ ) {
			sortedFiles[i] = fileWrappers[i].getFile();
		}

		return sortedFiles;
	}

	/**
	 * @param content
	 *            content need to write
	 * @param append
	 *            if <code>true</code>, then bytes will be written to the end of
	 *            the file rather than the beginning
	 * @throws IOException
	 */
	public static boolean write( String filePath, String content, boolean append ) throws IOException {
		FileWriter filewriter = null;
		try {
			File file = new File( filePath );
			filewriter = new FileWriter( file, append );
			filewriter.write( content );
			return true;
		} finally {
			IOUtil.closeWriter( filewriter );
		}
	}

	/**
	 * @param filePath
	 * @param content
	 * @param encode
	 * @return
	 */
	public static boolean write( String filePath, String content, String encode ) {
		try {
			OutputStreamWriter out = new OutputStreamWriter( new FileOutputStream( filePath ), encode );
			out.write( content );
			out.flush();
			out.close();
			return true;
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用GBK编码读取文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return String 文件内容
	 * @throws IOException
	 */
	public static String readFile( String filePath ) throws IOException {
		return readFile( filePath, EncodingConstant.GBK );
	}

	/**
	 * 使用指定编码读取文件,此方法无须用户关闭资源，方法内部已经全部关闭了。
	 * 
	 * @param filePath
	 *            文件路径
	 * @param encoding
	 *            读取的编码
	 * @return String 文件内容
	 * @throws IOException
	 */
	public static String readFile( String filePath, String encoding ) throws IOException {

		File file = new File( filePath );
		FileInputStream fileInputStream = null;
		StringBuilder content = new StringBuilder();
		try {
			fileInputStream = new FileInputStream( file );
		} catch ( FileNotFoundException e ) {
			throw e;
		}

		BufferedReader br = new BufferedReader( new InputStreamReader( fileInputStream, encoding ) );
		String data = null;
		try {
			while ( ( data = br.readLine() ) != null ) {
				content.append( data ).append( "\n" );
			}
			return content.toString();
		} catch ( IOException e ) {
			throw new IOException( "读取文件异常: " + e.getMessage() );
		} finally {
			try {
				fileInputStream.close();
				br.close();
			} catch ( IOException e ) {
			}
		}
	}

	/**
	 * Note: You need to close Reader.
	 * 
	 * @param filePath
	 *            文件路径
	 * @return String 文件内容
	 * @throws IOException
	 */
	public static Reader readFileReader( String filePath ) throws IOException {

		File file = new File( filePath );
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( file );
		} catch ( FileNotFoundException e ) {
			throw e;
		}
		return new InputStreamReader( fileInputStream );
	}

	/**
	 * 使用指定编码读取文件,此方法需要用户关闭资源
	 * 
	 * @param filePath
	 *            文件路径
	 * @param encoding
	 *            读取的编码
	 * @return String 文件内容
	 * @throws IOException
	 */
	public static BufferedReader readFileReturnBufferedReader( String filePath, String encoding ) throws IOException {

		File file = new File( filePath );
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( file );
		} catch ( FileNotFoundException e ) {
			throw e;
		}
		return new BufferedReader( new InputStreamReader( fileInputStream, encoding ) );
	}

	/**
	 * Read properties file.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Properties readPropertyFile( String filePath ) throws IOException {

		Properties properties = new Properties();
		Reader reader = null;
		try {
			reader = FileUtil.readFileReader( filePath );
			properties.load( reader );
			return properties;
		} finally {
			IOUtil.closeReader( reader );
		}
	}

}

@SuppressWarnings( "rawtypes" )
class FileWrapper implements Comparable {
	/** File */
	private File file;

	public FileWrapper( File file ) {
		this.file = file;
	}

	public int compareTo( Object obj ) {
		assert obj instanceof FileWrapper;

		FileWrapper castObj = ( FileWrapper ) obj;

		if ( this.file.getName().compareTo( castObj.getFile().getName() ) > 0 ) {
			return 1;
		} else if ( this.file.getName().compareTo( castObj.getFile().getName() ) < 0 ) {
			return -1;
		} else {
			return 0;
		}
	}

	public File getFile() {
		return this.file;
	}
}
