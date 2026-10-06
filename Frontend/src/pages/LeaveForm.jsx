import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import toast from 'react-hot-toast';
import {leaveApi} from '../services/api';

export default function LeaveForm() {
  const nav = useNavigate();
  const [form, setForm] = useState({ leaveType: 'CASUAL', startDate: '', endDate: '', reason: '' });
  const [busy, setBusy] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await leaveApi.create(form);
      toast.success('Leave request submitted successfully!');
      nav('/leaves');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Unable to submit request.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="page narrow">
      <div className="section-head">
        <div>
          <span className="eyebrow">TIME OFF</span>
          <h2>Request leave</h2>
          <p>Submit a request to HR. Your request will remain pending until reviewed.</p>
        </div>
      </div>
      
      <form className="large-form" onSubmit={submit}>
        <div className="form-row">
          <label>
            Leave type
            <select value={form.leaveType} onChange={(e) => setForm({ ...form, leaveType: e.target.value })}>
              {['CASUAL', 'SICK', 'ANNUAL', 'OTHER'].map(x => <option key={x}>{x}</option>)}
            </select>
          </label>
          <div className="form-info">
            <strong>Approval workflow</strong>
            <span>Employee &rarr; HR review &rarr; decision + note</span>
          </div>
        </div>
        
        <div className="form-row">
          <label>
            Start date
            <input required type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
          </label>
          <label>
            End date
            <input required type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
          </label>
        </div>
        
        <label>
          Reason
          <textarea required rows="6" maxLength="500" value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} placeholder="Briefly explain why you need leave..." />
        </label>
        
        <div className="form-actions">
          <button className="primary-btn" disabled={busy}>{busy ? 'Submitting...' : 'Submit request'}</button>
          <button type="button" className="secondary-btn" onClick={() => nav('/leaves')}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
