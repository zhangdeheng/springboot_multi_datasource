package com.springboot.demo.base;
/*package com.rainierSoft.virtualexperiment.base;
import com.googlecode.silverframework.core.ClientInfoHolder;
import com.googlecode.silverframework.core.entity.Auditable;
import com.googlecode.silverframework.core.entity.Component;
import com.googlecode.silverframework.core.entity.Entity;
import com.googlecode.silverframework.core.service.AbsVerifyService;
import com.googlecode.silverframework.sys.dao.OperationLogDao;
import com.googlecode.silverframework.sys.entity.FieldLog;
import com.googlecode.silverframework.sys.entity.OperationLog;
import com.googlecode.silverframework.sys.entity.OperationLog.LogType;
import com.googlecode.silverframework.sys.service.EntityConfiguration;
import com.googlecode.silverframework.sys.service.LogVisitor;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.Configuration.PrimitiveDefaultValueMode;
import de.danielbechler.diff.node.Node;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly = true)
public abstract class BaseService<T extends Entity> extends AbsVerifyService {
	private static final ObjectDiffer objectDiffer;
	@Autowired
	protected OperationLogDao operationLogDao;

	protected abstract PagingAndSortingRepository<T, Long> getDao();

	public T getForUpdate(long id) {
		Entity t = (Entity) this.getDao().findOne(Long.valueOf(id));
		if (t instanceof Auditable) {
			try {
				Entity e = (Entity) BeanUtils.cloneBean(t);
				((Auditable) t).setOrig(e);
				this.copyComponentProps(e, t);
			} catch (InstantiationException | InvocationTargetException | NoSuchMethodException
					| IllegalAccessException arg4) {
				this.logger.error(ExceptionUtils.getStackTrace(arg4));
				throw new RuntimeException(arg4);
			}
		}

		return t;
	}

	private void copyComponentProps(T copy, T orig) {
		PropertyDescriptor[] descriptors = BeanUtilsBean.getInstance().getPropertyUtils()
				.getPropertyDescriptors(orig.getClass());
		PropertyDescriptor[] arg3 = descriptors;
		int arg4 = descriptors.length;

		for (int arg5 = 0; arg5 < arg4; ++arg5) {
			PropertyDescriptor pd = arg3[arg5];
			if (Component.class.isAssignableFrom(pd.getPropertyType())) {
				Method getter = pd.getReadMethod();
				Method setter = pd.getWriteMethod();

				try {
					Object e = getter.invoke(orig, new Object[0]);
					if (null == e) {
						setter.invoke(copy, new Object[] { null });
					} else {
						Object copyValue = e.getClass().newInstance();
						org.springframework.beans.BeanUtils.copyProperties(e, copyValue);
						setter.invoke(copy, new Object[] { copyValue });
					}
				} catch (Exception arg11) {
					this.logger.error(ExceptionUtils.getStackTrace(arg11));
					throw new IllegalArgumentException();
				}
			}
		}

	}

	@Transactional(readOnly = false)
	public T saveWithAudit(T t) {
		boolean willLog = false;
		boolean isEdit = false;
		if (ClientInfoHolder.needOperationLog() && t instanceof Auditable) {
			willLog = true;
			if (((Auditable) t).getIdStr() != null) {
				isEdit = true;
			}
		}

		Entity orig = isEdit ? ((Auditable) t).getOrig() : null;
		this.getDao().save(t);
		if (willLog) {
			Auditable a = (Auditable) t;
			OperationLog log = new OperationLog(ClientInfoHolder.getClientInfo());
			log.setAuditInfo(a);
			log.setType(isEdit ? LogType.Update : LogType.Create);
			Iterator arg6 = this.generateFieldLogs(orig, a).iterator();

			while (arg6.hasNext()) {
				FieldLog fl = (FieldLog) arg6.next();
				fl.setLog(log);
				log.getFieldLogs().add(fl);
			}

			this.operationLogDao.save(log);
		}

		return t;
	}

	@Transactional(readOnly = false)
	public void deleteWithAudit(T t) {
		if (ClientInfoHolder.needOperationLog() && t instanceof Auditable) {
			Auditable a = (Auditable) t;
			OperationLog log = new OperationLog(ClientInfoHolder.getClientInfo());
			log.setAuditInfo(a);
			log.setType(LogType.Remove);
			Iterator arg3 = this.generateFieldLogs(t, (Object) null).iterator();

			while (arg3.hasNext()) {
				FieldLog fl = (FieldLog) arg3.next();
				fl.setLog(log);
				log.getFieldLogs().add(fl);
			}

			this.operationLogDao.save(log);
		}

		this.getDao().delete(t);
	}

	@Transactional(readOnly = false)
	public void deleteWithAudit(Long id) {
		Entity t = (Entity) this.getDao().findOne(id);
		this.deleteWithAudit(t);
	}

	@Transactional(readOnly = false)
	public void deleteWithAudit(Long[] ids) {
		Long[] arg1 = ids;
		int arg2 = ids.length;

		for (int arg3 = 0; arg3 < arg2; ++arg3) {
			Long l = arg1[arg3];
			this.deleteWithAudit(l);
		}

	}

	@Transactional(readOnly = false)
	public void deleteWithAudit(Iterable<? extends T> entities) {
		Iterator arg1 = entities.iterator();

		while (arg1.hasNext()) {
			Entity entity = (Entity) arg1.next();
			this.deleteWithAudit(entity);
		}

	}

	protected List<FieldLog> generateFieldLogs(Object orig, Object a) {
		Node root = objectDiffer.compare(a, orig);
		LogVisitor visitor = new LogVisitor(a, orig);
		root.visit(visitor);
		return visitor.getFieldLogs();
	}

	@Transactional(readOnly = false)
	public <S extends T> S save(S entity) {
		return (Entity) this.getDao().save(entity);
	}

	@Transactional(readOnly = false)
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return this.getDao().save(entities);
	}

	public T findOne(Long id) {
		return (Entity) this.getDao().findOne(id);
	}

	public Iterable<T> findAll() {
		return this.getDao().findAll();
	}

	public Iterable<T> findAll(Iterable<Long> ids) {
		return this.getDao().findAll(ids);
	}

	public Iterable<T> findAll(Sort sort) {
		return this.getDao().findAll(sort);
	}

	public Page<T> findAll(Pageable pageable) {
		return this.getDao().findAll(pageable);
	}

	public Long count() {
		return Long.valueOf(this.getDao().count());
	}

	@Transactional(readOnly = false)
	public void delete(T entity) {
		this.getDao().delete(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Long id) {
		this.getDao().delete(id);
	}

	@Transactional(readOnly = false)
	public void delete(Iterable<? extends T> entities) {
		this.getDao().delete(entities);
	}

	public boolean exists(Long id) {
		return this.getDao().exists(id);
	}

	protected void saveUpdateLog(Auditable<?> a, String property, String oldVal, String newVal) {
		OperationLog log = new OperationLog(ClientInfoHolder.getClientInfo());
		log.setAuditInfo(a);
		log.setType(LogType.Update);
		FieldLog fl = new FieldLog();
		fl.setName(property);
		fl.setOldVal(oldVal);
		fl.setNewVal(newVal);
		fl.setLog(log);
		log.getFieldLogs().add(fl);
		this.operationLogDao.save(log);
	}

	protected void saveUpdateLog(Auditable<?> a) {
		OperationLog log = new OperationLog(ClientInfoHolder.getClientInfo());
		log.setAuditInfo(a);
		log.setType(LogType.Update);
		Entity oldObject = a.getOrig();
		Iterator arg3 = this.generateFieldLogs(oldObject, a).iterator();

		while (arg3.hasNext()) {
			FieldLog fl = (FieldLog) arg3.next();
			fl.setLog(log);
			log.getFieldLogs().add(fl);
		}

		this.operationLogDao.save(log);
	}

	public boolean checkDbData() {
		return this.checkDbData(this.getDao(), new String[0]);
	}

	static {
		ConvertUtils.register(new DateConverter((Object) null), Date.class);
		ConvertUtils.register(new SqlDateConverter((Object) null), java.sql.Date.class);
		ConvertUtils.register(new SqlTimeConverter((Object) null), Time.class);
		ConvertUtils.register(new SqlTimestampConverter((Object) null), Timestamp.class);
		ConvertUtils.register(new BooleanConverter((Object) null), Boolean.class);
		ConvertUtils.register(new ShortConverter((Object) null), Short.class);
		ConvertUtils.register(new IntegerConverter((Object) null), Integer.class);
		ConvertUtils.register(new LongConverter((Object) null), Long.class);
		ConvertUtils.register(new FloatConverter((Object) null), Float.class);
		ConvertUtils.register(new DoubleConverter((Object) null), Double.class);
		ConvertUtils.register(new BigDecimalConverter((Object) null), BigDecimal.class);
		ConvertUtils.register(new BigIntegerConverter((Object) null), BigInteger.class);
		objectDiffer = ObjectDifferFactory.getInstance(
				(new EntityConfiguration()).treatPrimitiveDefaultValuesAs(PrimitiveDefaultValueMode.ASSIGNED));
	}
}
*/