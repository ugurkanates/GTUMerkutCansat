# webcam-capture-driver-vlcj

This is capture driver which uses [vlcj](http://www.capricasoftware.co.uk/projects/vlcj/index.html) library from [Caprica Software Limited](http://www.capricasoftware.co.uk/) to gain access to the camera device.

**NOTE!** On Windows one needs to provide list of webcam devices manually because ```vlclib``` does not implement video devices discovery on this platform (please see [this example](https://github.com/sarxos/webcam-capture/blob/master/webcam-capture-drivers/driver-vlcj/src/example/java/WebcamPanelForWindows.java) to find out how this should be done).

The vlcj library is distributed according to the terms of the [GPL](http://www.gnu.org/licenses/gpl.html) license.

## Maven

Stable:

```xml
<dependency>
	<groupId>com.github.sarxos</groupId>
	<artifactId>webcam-capture-driver-vlcj</artifactId>
	<version>0.3.12</version>
</dependency>
```

Snapshot:

```xml
<repository>
    <id>Sonatype OSS Snapshot Repository</id>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.sarxos</groupId>
    <artifactId>webcam-capture-driver-vlcj</artifactId>
    <version>0.3.13-SNAPSHOT</version>
</dependency>
```

## How To Use

Set capture driver before you start using Webcam class:

```java
public class TakePictureExample {

	// set capture driver for fswebcam tool
	static {
		Webcam.setDriver(new VlcjDriver());
	}

	public static void main(String[] args) throws IOException {

		// get default webcam and open it
		Webcam webcam = Webcam.getDefault();
		webcam.open();

		// get image from webcam device
		BufferedImage image = webcam.getImage();

		// save image to PNG file
		ImageIO.write(image, "JPG", new File("test.jpg"));

		// close webcam
		webcam.close();
	}
}
```

## Capture Driver License

Copyright (C) 2012 - 2017 Bartosz Firyn <https://github.com/sarxos>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

