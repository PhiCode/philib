/*
 * Copyright (c) 2013 Philipp Meinen <philipp@bind.ch>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ch.bind.philib.conf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ch.bind.philib.io.SafeCloseUtil;
import ch.bind.philib.lang.CompareUtil;
import ch.bind.philib.util.CowSet;
import ch.bind.philib.validation.Validation;

/**
 * @author Philipp Meinen
 */
public class Config {

	private final CowSet<ConfigValueListener> listeners = new CowSet<ConfigValueListener>(ConfigValueListener.class);

	private final URL[] urls;

	private boolean loading;

	private volatile Map<String, String> config;

	public Config(URL url) {
		Validation.notNull(url);
		urls = new URL[] { url };
	}

	public Config(URL[] urls) {
		Validation.notNullOrEmpty(urls);
		this.urls = Arrays.copyOf(urls, urls.length);
	}

	public Config(Collection<URL> urls) {
		Validation.notNull(urls);
		this.urls = urls.toArray(new URL[urls.size()]);
		Validation.notNullOrEmpty(this.urls);
	}

	public void addListener(ConfigValueListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ConfigValueListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Loads all configuration urls. At least one URL
	 * 
	 * @throws IOException in case no url could be opened.
	 */
	public synchronized void load() throws IOException {
		if (loading) {
			return;
		}
		loading = true;
		int numSuccess = 0;
		Exception lastExc = null;
		Map<String, String> newConfig = new HashMap<String, String>();
		try {
			for (URL url : urls) {
				InputStream is = null;
				try {
					is = url.openStream();
					Properties props = new Properties();
					props.load(is);
					Map<String, String> m = toMap(props);
					newConfig.putAll(m);
					numSuccess++;
				} catch (IOException e) {
					lastExc = e;
				} finally {
					SafeCloseUtil.close(is);
				}
			}
			if (numSuccess > 0) {
				if (config != null) {
					notifyDifferences(newConfig, config);
				} else {
					notifyAdded(newConfig);
				}
				config = newConfig;
			} else {
				throw new IOException("no resources found", lastExc);
			}
		} finally {
			loading = false;
		}
	}

	public static Map<String, String> toMap(Properties p) {
		Map<String, String> m = new HashMap<String, String>();
		for (String key : p.stringPropertyNames()) {
			m.put(key, p.getProperty(key));
		}
		return m;
	}

	private void notifyDifferences(Map<String, String> newConfig, Map<String, String> oldConfig) {
		if (listeners.isEmpty()) {
			return;
		}
		Set<String> newKeys = newConfig.keySet();
		Set<String> oldKeys = oldConfig.keySet();
		for (String newKey : newKeys) {
			if (oldKeys.contains(newKey)) {
				String valNew = newConfig.get(newKey);
				String valOld = oldConfig.get(newKey);
				if (!CompareUtil.equals(valNew, valOld)) {
					notifyChanged(newKey, valOld, valNew);
				}
			} else {
				notifyAdded(newKey, newConfig.get(newKey));
			}
		}
		for (String key : oldKeys) {
			if (!newKeys.contains(key)) {
				notifyRemoved(key, oldConfig.get(key));
			}
		}
	}

	private void notifyAdded(Map<String, String> m) {
		if (listeners.isEmpty()) {
			return;
		}
		for (String key : m.keySet()) {
			notifyAdded(key, m.get(key));
		}
	}

	private void notifyAdded(String key, String value) {
		for (ConfigValueListener l : listeners.getView()) {
			l.added(key, value);
		}
	}

	private void notifyRemoved(String key, String oldValue) {
		for (ConfigValueListener l : listeners.getView()) {
			l.removed(key, oldValue);
		}
	}

	private void notifyChanged(String key, String oldValue, String newValue) {
		for (ConfigValueListener l : listeners.getView()) {
			l.changed(key, oldValue, newValue);
		}
	}

	public String get(String key) {
		Map<String, String> c = config;
		return c == null ? null : c.get(key);
	}
}
